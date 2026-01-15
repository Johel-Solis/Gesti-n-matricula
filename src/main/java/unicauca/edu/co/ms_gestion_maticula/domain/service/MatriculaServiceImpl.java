package unicauca.edu.co.ms_gestion_maticula.domain.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.enums.EstadoEstudianteMaestria;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Curso;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Estudiante;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Matricula;
import unicauca.edu.co.ms_gestion_maticula.domain.model.MatriculaCurso;
import unicauca.edu.co.ms_gestion_maticula.domain.model.PeriodoAcademico;
import unicauca.edu.co.ms_gestion_maticula.domain.request.CursoMatriculaEstudiantesRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.request.CursoMatriculaRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.request.EstudianteMatriculaRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.request.ListEstudianteRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.request.MatriculaCursoEstudiantesRequests;
import unicauca.edu.co.ms_gestion_maticula.domain.request.MatriculaEstudianteCursosRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.In.MatriculaService;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.out.CursoRepository;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.out.MatriculaRepository;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.out.PeriodoAcademicoRepository;
import unicauca.edu.co.ms_gestion_maticula.domain.response.CursoResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.EstudianteMatriculaResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.EstudianteResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MatriculaAgrupadaResonse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MatriculaBatchResultResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MatriculaCursoResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MatriculaEstudianteCursosResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MatriculaNoRealizadaResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.PeriodoAcademicoResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MatriculaResponse;

@Service
@RequiredArgsConstructor
public class MatriculaServiceImpl implements MatriculaService {

    @Autowired
    private final MatriculaRepository matriculaRepository;
    @Autowired
    private final CursoRepository cursoRepository;
    @Autowired
    private final PeriodoAcademicoRepository periodoAcademicoRepository;
    @Autowired
    private final ModelMapper modelMapper;

    @Override
    public MatriculaBatchResultResponse matricularEstudiantesEnCursos(MatriculaCursoEstudiantesRequests requests) {
        if (requests == null || requests.getMatriculaEstudianteCursos() == null ||
            requests.getMatriculaEstudianteCursos().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos una solicitud de matrícula");
        }
        
        List<MatriculaResponse> exitos = new ArrayList<>();
        List<MatriculaNoRealizadaResponse> fallidos = new ArrayList<>();

        // Procesar cada solicitud de matrícula
        for (MatriculaEstudianteCursosRequest solicitud : requests.getMatriculaEstudianteCursos()) {
            
            for (CursoMatriculaRequest cursoRequest : solicitud.getCursos()) {
                Long cursoId = cursoRequest.getCursoId();
                try {
                validarMatriculaEstudiantes(solicitud.getEstudianteId(), cursoId);
                Curso curso = validarYObtenerCurso(cursoId);
                Matricula matricula = crearMatricula(solicitud.getEstudianteId(), curso, cursoRequest.getObservacion());
                Matricula matriculaResult = matriculaRepository.save(matricula);
                exitos.add(modelMapper.map(matriculaResult, MatriculaResponse.class));
                } catch (Exception e) {
                    CursoResponse cursoResponse = cursoRepository.findCursoById(cursoId)
                            .map(curso -> modelMapper.map(curso, CursoResponse.class))
                            .orElse(null);
                    Estudiante estudiante =matriculaRepository.getEstudianteByIdAndEstado(solicitud.getEstudianteId(), EstadoEstudianteMaestria.ACTIVO)
                    .orElseThrow(null);
                    fallidos.add(MatriculaNoRealizadaResponse.builder()
                            .estudiante(modelMapper.map(estudiante, EstudianteResponse.class))
                            .curso(cursoResponse)
                            .motivo(e.getMessage())
                            .build());
                }
            }
        }

        return MatriculaBatchResultResponse.builder()
                .matriculasRealizadas(exitos)
                .matriculasNoRealizadas(fallidos)
                .build();
    }

    @Override
    public MatriculaEstudianteCursosResponse matriculaEstudianteCursos(MatriculaEstudianteCursosRequest request) {

        if (request == null || request.getEstudianteId() == null) {
            throw new IllegalArgumentException("La solicitud de matrícula es requerida");
        }

        // Validar periodo de matrícula
        validarPeriodoMatricula();

        Estudiante estudiante =matriculaRepository.getEstudianteByIdAndEstado(request.getEstudianteId(), EstadoEstudianteMaestria.ACTIVO)
                .orElseThrow(() -> new EntityNotFoundException("No está activo o no existe el estudiante con ID: " + request.getEstudianteId()));

        List<Matricula> matriculasExistentes = matriculaRepository.findByEstudianteIdAndPeriodoActivo(request.getEstudianteId());

        Map<Long, Matricula> matriculaPorCursoId = new HashMap<>();
        for (Matricula matricula : matriculasExistentes) {
            if (matricula.getCurso() != null && matricula.getCurso().getId() != null) {
                matriculaPorCursoId.put(matricula.getCurso().getId(), matricula);
            }
        }
        

        List<MatriculaNoRealizadaResponse> fallidos = new ArrayList<>();

        Map<Long, CursoMatriculaRequest> solicitudesPorCursoId = new HashMap<>();
        for (CursoMatriculaRequest cursoRequest : request.getCursos()) {
            if (cursoRequest == null || cursoRequest.getCursoId() == null) {
                fallidos.add(MatriculaNoRealizadaResponse.builder()
                        .estudiante(modelMapper.map(estudiante, EstudianteResponse.class))
                        .curso(null)
                        .motivo("El cursoId es requerido")
                        .build());
                continue;
            }
            solicitudesPorCursoId.putIfAbsent(cursoRequest.getCursoId(), cursoRequest);
        }

        

        List<Matricula> matriculasEliminadas = new ArrayList<>();
        for (Matricula matricula : matriculasExistentes) {
            
            Long cursoId = matricula.getCurso() != null ? matricula.getCurso().getId() : null;
            if (cursoId != null && !solicitudesPorCursoId.containsKey(cursoId)) {
                matriculaRepository.deleteById(matricula.getId());
                matriculasEliminadas.add(matricula);
            }
        }

        List<Matricula> matriculasProcesadas = new ArrayList<>();
        for (CursoMatriculaRequest cursoRequest : request.getCursos()) {
            Long cursoId = cursoRequest.getCursoId();
            Matricula existente = matriculaPorCursoId.get(cursoId);
            if (existente != null) {
                
                if (cursoRequest.getObservacion() != null &&
                        !Objects.equals(cursoRequest.getObservacion(), existente.getObservacion())) {
                    existente.setObservacion(cursoRequest.getObservacion());
                    existente = matriculaRepository.update(existente);
                    matriculasProcesadas.add(existente);
                }else {
                    fallidos.add(MatriculaNoRealizadaResponse.builder()
                            .estudiante(modelMapper.map(estudiante, EstudianteResponse.class))
                            .curso(modelMapper.map(existente.getCurso(), CursoResponse.class))
                            .motivo("El estudiante ya está matriculado en este curso")
                            .build());
                }
                
                continue;
            }

            try {
            validarMatriculaEstudiantes(request.getEstudianteId(), cursoId);
            Curso curso = validarYObtenerCurso(cursoId);
            Matricula matricula = crearMatricula(request.getEstudianteId(), curso, cursoRequest.getObservacion());
            Matricula matriculaResult = matriculaRepository.save(matricula);
            matriculasProcesadas.add(matriculaResult);
            } catch (Exception e) {
                CursoResponse cursoResponse = cursoRepository.findCursoById(cursoId)
                        .map(curso -> modelMapper.map(curso, CursoResponse.class))
                        .orElse(null);
                fallidos.add(MatriculaNoRealizadaResponse.builder()
                        .estudiante(modelMapper.map(estudiante, EstudianteResponse.class))
                        .curso(cursoResponse)
                        .motivo(e.getMessage())
                        .build());
            }
        }

        return MatriculaEstudianteCursosResponse.builder()
                .matriculasProcesadas(toMatriculaResponse(matriculasProcesadas))
                .matriculasNoProcesadas(fallidos)
                .matriculasEliminadas(toMatriculaResponse(matriculasEliminadas))
                .build();
    }


    @Override
    public MatriculaEstudianteCursosResponse matricularCursoEstudiantes(CursoMatriculaEstudiantesRequest request) {
          if (request == null || request.getCursoId() == null) {
            throw new IllegalArgumentException("La solicitud de matrícula es requerida");
        }
        // Validar periodo de matrícula
        validarPeriodoMatricula();

        Curso curso = validarYObtenerCurso(request.getCursoId());

        List<Matricula> matriculasExistentes = matriculaRepository.findByCursoIdAndPeriodoId(request.getCursoId(),curso.getPeriodo().getId());
        
        Map<Long, Matricula> matriculaPorEstudianteId = new HashMap<>();
        for (Matricula matricula : matriculasExistentes) {
            if (matricula.getEstudiante() != null && matricula.getEstudiante().getId() != null) {
                matriculaPorEstudianteId.put(matricula.getEstudiante().getId(), matricula);
            }
        }
    

        List<MatriculaNoRealizadaResponse> fallidos = new ArrayList<>();

        Map<Long, EstudianteMatriculaRequest> solicitudesPorEstudianteId = new HashMap<>();
        for (EstudianteMatriculaRequest estudianteRequest : request.getEstudiantes()) {
            if (estudianteRequest == null || estudianteRequest.getEstudianteId() == null) {
                fallidos.add(MatriculaNoRealizadaResponse.builder()
                        .estudiante(null)
                        .curso(modelMapper.map(curso, CursoResponse.class))
                        .motivo("El estudianteId es requerido")
                        .build());
                continue;
            }
            solicitudesPorEstudianteId.putIfAbsent(estudianteRequest.getEstudianteId(), estudianteRequest);
        }

        List<Matricula> matriculasEliminadas = new ArrayList<>();
        for (Matricula matricula : matriculasExistentes) {
            Long estudianteId = matricula.getEstudiante() != null ? matricula.getEstudiante().getId() : null;
            if (estudianteId != null && !solicitudesPorEstudianteId.containsKey(estudianteId)) {
                matriculaRepository.deleteById(matricula.getId());
                matriculasEliminadas.add(matricula);
            }
        }

        List<Matricula> matriculasProcesadas = new ArrayList<>();
        for (EstudianteMatriculaRequest estudianteRequest : request.getEstudiantes()) {
            Long estudianteId = estudianteRequest.getEstudianteId();
            Matricula existente = matriculaPorEstudianteId.get(estudianteId);
            if (existente != null) {
                
                if (estudianteRequest.getObservacion() != null &&
                        !Objects.equals(estudianteRequest.getObservacion(), existente.getObservacion())) {
                    existente.setObservacion(estudianteRequest.getObservacion());
                    existente = matriculaRepository.update(existente);
                    matriculasProcesadas.add(existente);
                }else {
                    Estudiante estudiante =matriculaRepository.getEstudianteByIdAndEstado(estudianteRequest.getEstudianteId(), EstadoEstudianteMaestria.ACTIVO)
                .orElseThrow(null);
                    fallidos.add(MatriculaNoRealizadaResponse.builder()
                            .estudiante(modelMapper.map(estudiante, EstudianteResponse.class))
                            .curso(modelMapper.map(curso, CursoResponse.class))
                            .motivo("El estudiante ya está matriculado en este curso")
                            .build());
                }
                
                continue;
            }

            try {
            validarMatriculaEstudiantes(estudianteRequest.getEstudianteId(), curso.getId());
            Matricula matricula = crearMatricula(estudianteRequest.getEstudianteId(), curso, estudianteRequest.getObservacion());
            Matricula matriculaResult = matriculaRepository.save(matricula);
            matriculasProcesadas.add(matriculaResult);
            } catch (Exception e) {
                Estudiante estudiante =matriculaRepository.getEstudianteByIdAndEstado(estudianteRequest.getEstudianteId(), EstadoEstudianteMaestria.ACTIVO)
                .orElseThrow(null);
                fallidos.add(MatriculaNoRealizadaResponse.builder()
                        .estudiante(modelMapper.map(estudiante, EstudianteResponse.class))
                        .curso(modelMapper.map(curso, CursoResponse.class))
                        .motivo(e.getMessage())
                        .build());
            }
        }

        return MatriculaEstudianteCursosResponse.builder()
                .matriculasProcesadas(toMatriculaResponse(matriculasProcesadas))
                .matriculasNoProcesadas(fallidos)
                .matriculasEliminadas(toMatriculaResponse(matriculasEliminadas))
                .build();
    }

    @Override
    public Boolean validarMatriculaEstudiantes(Long estudianteId, Long cursoId) {
        if (estudianteId == null || cursoId == null) {
            throw new IllegalArgumentException("Los parámetros estudianteId y cursoId son requeridos");
        }
        // Validar que el estudiante esté activo
        matriculaRepository.getEstudianteByIdAndEstado(estudianteId,EstadoEstudianteMaestria.ACTIVO)
                .orElseThrow(() -> new EntityNotFoundException("No está activo o no existe el estudiante con ID: " + estudianteId));

        // Validar periodo de matrícula
        validarPeriodoMatricula();
        
        // Obtener y validar el curso
        Curso curso = validarYObtenerCurso(cursoId);
        
        // Validar prerequisitos de la asignatura
        validarPrerequisitos(estudianteId, curso.getAsignatura().getId());
        
        // Obtener periodo académico activo
        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo académico activo"));
        
        // Verificar si el estudiante ya está matriculado en esta asignatura en este periodo
        boolean yaMatriculado = matriculaRepository.existsMatriculaByEstudianteIdAndPeriodoIdAndAsignaturaId(
                estudianteId, periodoActivo.getId(), curso.getAsignatura().getId());

        
        if (yaMatriculado) {
            throw new IllegalArgumentException("El estudiante ya está matriculado en esta asignatura para el periodo actual");
        }
        
        // Verificar si el estudiante ya ganó la asignatura
        boolean asignaturaGanada = matriculaRepository.asignaturaGanada(estudianteId, curso.getAsignatura().getId());
        if (asignaturaGanada) {
            throw new IllegalArgumentException("El estudiante ya ganó esta asignatura");
        }
        
        return true;
    }

    @Override
    public List<Matricula> consultarMatriculaEstudiantes(ListEstudianteRequest requests) {
        if (requests == null || requests.getEstudianteIds() == null || 
            requests.getEstudianteIds().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos un estudiante para consultar");
        }
        
        List<Matricula> todasLasMatriculas = new ArrayList<>();
        
        // Obtener matrículas para cada estudiante especificado
        for (Long estudianteId : requests.getEstudianteIds()) {
            if (estudianteId != null) {
                List<Matricula> matriculasEstudiante = matriculaRepository.findByEstudianteId(estudianteId);
                todasLasMatriculas.addAll(matriculasEstudiante);
            }
        }
        
        return todasLasMatriculas;
    }

    @Override
    public List<Asignatura> obtenerAsignaturasDisponiblesporEstudiante(Long estudianteId) {
        if (estudianteId == null) {
            throw new IllegalArgumentException("El ID del estudiante es requerido");
        }
        
        System.out.println("Estudiante ID recibido en el servicio: " + estudianteId);
        // Validar existencia del estudiante
        matriculaRepository.getEstudianteById(estudianteId)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con ID: " + estudianteId));

        // Validar periodo de matrícula
        validarPeriodoMatricula();
        
        // Obtener periodo académico activo
        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo académico activo"));
        
        // Obtener todas las asignaturas activas
        List<Asignatura> todasLasAsignaturas = cursoRepository.findAsignaturasByStatus(true,null);
        
        // Obtener asignaturas ya matriculadas por el estudiante en el periodo actual
        List<Asignatura> asignaturasMatriculadas = matriculaRepository.getAsignaturasMatriculadas(estudianteId, periodoActivo.getId());
        
        // Filtrar asignaturas disponibles
        List<Asignatura> asignaturasDisponibles = todasLasAsignaturas.stream()
                .filter(asignatura -> {
                    // Verificar que no esté ya matriculado
                    boolean yaMatriculado = asignaturasMatriculadas.stream()
                            .anyMatch(matriculada -> matriculada.getId().equals(asignatura.getId()));
                    
                    // Verificar que no haya ganado la asignatura
                    boolean yaGanada = matriculaRepository.asignaturaGanada(estudianteId, asignatura.getId());
                    
                    return !yaMatriculado && !yaGanada;
                })
                .collect(Collectors.toList());
        
        return asignaturasDisponibles;
    }

    /**
     * Método adicional para cancelar una matrícula específica
     */
    public void cancelarMatricula(Long matriculaId, String motivoCancelacion) {
        if (matriculaId == null) {
            throw new IllegalArgumentException("El ID de la matrícula es requerido");
        }
        
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new EntityNotFoundException("Matrícula no encontrada"));
        
        // Validar que la matrícula esté activa
        if (!matricula.isEstado()) {
            throw new IllegalArgumentException("Solo se pueden cancelar matrículas activas");
        }
        
        // Validar periodo de matrícula
        validarPeriodoMatricula();
        
        // Actualizar estado y observación
        matricula.setEstadoMatricula("CANCELADA");
        matricula.setEstado(false);
        matricula.setObservacion(matricula.getObservacion() + " - CANCELADA: " + motivoCancelacion);
        
        matriculaRepository.update(matricula);
    }

    /**
     * Método adicional para obtener matrículas por estudiante 
     */
    public List<EstudianteMatriculaResponse> obtenerMatriculasPorEstudiante(Long estudianteId) {
        if (estudianteId == null) {
            throw new IllegalArgumentException("El ID del estudiante es requerido");
        }
        //TODO: Validar existencia del estudiante, esta fallando la funcion
        
        matriculaRepository.getEstudianteById(estudianteId)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con ID: " + estudianteId));
        
        List<Matricula> todasLasMatriculas = matriculaRepository.findByEstudianteIdAndPeriodoActivo(estudianteId);
                         
        return todasLasMatriculas.stream()
                .map(this::toEstudianteMatriculaResponse)
                .collect(Collectors.toList());
        
    }

    /**
     * Método adicional para obtener una matrícula por ID
     */
    public Matricula obtenerMatriculaPorId(Long matriculaId) {
        if (matriculaId == null) {
            throw new IllegalArgumentException("El ID de la matrícula es requerido");
        }
        
        return matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new EntityNotFoundException("Matrícula no encontrada con ID: " + matriculaId));
    }

    /**
     * Método adicional para listar todas las matrículas con filtros
     */
    @Override
    public List<MatriculaAgrupadaResonse> listarMatriculas(Long periodoId, String estado, Long asignatura, Long estudiante){
        if (periodoId == null) {
            throw new IllegalArgumentException("El identificador del periodo es obligatorio");
        }

        List<MatriculaCurso> matriculasCurso = matriculaRepository.getListMatriculas(periodoId, estado, asignatura, estudiante);

        return matriculasCurso.stream()
                .map(this::toMatriculaAgrupadaResonse)
                .toList();   
            }


    
    
    /**
     * Método para obtener estudiantes matriculados en un curso específico
     */
     @Override
    public List<MatriculaCursoResponse> obtenerEstudiantesMatriculadosEnCurso(Long cursoId) {
        cursoRepository.findCursoById(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado con ID: " + cursoId));

        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo académico activo"));

        List<Matricula> matriculas = matriculaRepository.findByCursoIdAndPeriodoId(cursoId, periodoActivo.getId());

        return matriculas.stream()
                .map(this::toMatriculaCursoResponse)
                .collect(Collectors.toList());
    
    }


    /**
     * Método auxiliar para validar que el periodo académico esté activo y dentro del plazo de matrícula
     */
    private void validarPeriodoMatricula() {
        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo académico activo"));
        
        LocalDate fechaActual = LocalDate.now();
        
        if (fechaActual.isBefore(periodoActivo.getFechaInicio())) {
            throw new IllegalArgumentException("El periodo académico aún no ha iniciado");
        }
        
        if (fechaActual.isAfter(periodoActivo.getFechaFinMatricula())) {
            throw new IllegalArgumentException("El periodo de matrícula ha finalizado");
        }
    }

    /**
     * Método auxiliar para validar que un curso existe y está disponible para matrícula
     * @param  cursoId
     */
    private Curso validarYObtenerCurso(Long cursoId) {
        Curso curso = cursoRepository.findCursoById(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado con ID: " + cursoId));
        
        // Verificar que el curso pertenezca al periodo activo
        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo académico activo"));
        
        if (!curso.getPeriodo().getId().equals(periodoActivo.getId())) {
            throw new IllegalArgumentException("El curso no pertenece al periodo académico activo");
        }

        if (!curso.isEstado()) {
            throw new IllegalArgumentException("El curso no está disponible para matrícula");
        }

        return curso;
    }

    /**
     * Método auxiliar para crear una matrícula
     */
    private Matricula crearMatricula(Long estudianteId, Curso curso, String observacion) {
        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo académico activo"));
        Estudiante estudiante= matriculaRepository.getEstudianteById(estudianteId)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con ID: " + estudianteId));
        return Matricula.builder()
                .estudiante(estudiante)
                .curso(curso)
                .periodo(periodoActivo)
                .estado(true)
                .estadoMatricula("ACTIVA")
                .observacion(observacion)
                .build();
    }

    /**
     * Validar prerequisitos de una asignatura para un estudiante antes de la matrícula.
     * (Actualmente sólo valida estado de la asignatura; extender si hay tabla de prerequisitos)
     */
    private void validarPrerequisitos(Long estudianteId, Long asignaturaId) {
        
        
        Asignatura asignatura = cursoRepository.findAsignaturaById(asignaturaId)
                .orElseThrow(() -> new EntityNotFoundException("Asignatura no encontrada"));
        
        if (!asignatura.getEstado()) {
            throw new IllegalArgumentException("La asignatura no está disponible para matrícula");
        }
    }

    private Long obtenerCursoIdPrimerIntento(MatriculaEstudianteCursosRequest solicitud) {
        if (solicitud.getCursos() == null || solicitud.getCursos().isEmpty()) {
            return null;
        }
        return solicitud.getCursos().get(0).getCursoId();
    }
    

    private List<MatriculaResponse> toMatriculaResponse(List<Matricula> matriculas) {
        return matriculas.stream()
                .map(matricula -> MatriculaResponse.builder()
                        .id(matricula.getId())
                        .estudiante(modelMapper.map(matricula.getEstudiante(), EstudianteResponse.class))
                        .curso(modelMapper.map(matricula.getCurso(), CursoResponse.class))
                        .periodo(modelMapper.map(matricula.getPeriodo(), PeriodoAcademicoResponse.class))
                        .estado(matricula.getEstadoMatricula())
                        .observacion(matricula.getObservacion())
                        .build())
                .collect(Collectors.toList());
    }

    private MatriculaAgrupadaResonse toMatriculaAgrupadaResonse(MatriculaCurso mCurso){
        return MatriculaAgrupadaResonse.builder()
        .idCurso(mCurso.getCurso().getId())
        .asignatura(mCurso.getCurso().getAsignatura().getNombre())
        .grupo(mCurso.getCurso().getGrupo())
        .periodo(modelMapper.map(mCurso.getCurso().getPeriodo(),PeriodoAcademicoResponse.class))
        .estado(mCurso.getCurso().isEstado()?"ACTIVO":"INACTIVO")
        .cantidadEstudiante(mCurso.getTotalMatriculas())
        .build();
    }

    private EstudianteMatriculaResponse toEstudianteMatriculaResponse(Matricula matricula){
        return EstudianteMatriculaResponse.builder()
        .id(matricula.getId())
        .curso(modelMapper.map(matricula.getCurso(), CursoResponse.class))
        .estado(matricula.getEstadoMatricula())
        .observacion(matricula.getObservacion())
        .build();
    }

     private MatriculaCursoResponse toMatriculaCursoResponse(Matricula matricula){
        return MatriculaCursoResponse.builder()
        .id(matricula.getId())
        .estudiante(modelMapper.map(matricula.getEstudiante(), EstudianteResponse.class))
        .estado(matricula.getEstadoMatricula())
        .observacion(matricula.getObservacion())
        .build();
    }

   
	

}
