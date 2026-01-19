package unicauca.edu.co.ms_gestion_maticula.domain.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Docente;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Persona;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.In.EmailService;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.out.EstudianteDocenteRepository;
import unicauca.edu.co.ms_gestion_maticula.domain.response.ReportCursoDto;
import unicauca.edu.co.ms_gestion_maticula.domain.response.TutorNotificacionResponse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.enums.EstadoEstudianteMaestria;
import unicauca.edu.co.ms_gestion_maticula.domain.enums.MatriculaEstado;
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
import unicauca.edu.co.ms_gestion_maticula.domain.request.MatriculaEstadoRequest;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(MatriculaServiceImpl.class);

    @Autowired
    private final MatriculaRepository matriculaRepository;
    @Autowired
    private final CursoRepository cursoRepository;
    @Autowired
    private final PeriodoAcademicoRepository periodoAcademicoRepository;
    @Autowired
    private final ModelMapper modelMapper;
    @Autowired
    private final EstudianteDocenteRepository estudianteDocenteRepository;
    @Autowired
    private final EmailService emailService;

    
    @Override
    public MatriculaBatchResultResponse matricularEstudiantesEnCursos(MatriculaCursoEstudiantesRequests requests) {
        if (requests == null || requests.getMatriculaEstudianteCursos() == null ||
            requests.getMatriculaEstudianteCursos().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos una solicitud de matrÃ­cula");
        }
    
        List<MatriculaResponse> exitos = new ArrayList<>();
        List<MatriculaNoRealizadaResponse> fallidos = new ArrayList<>();
        // Procesar cada solicitud de matrÃ­cula
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
                .matriculasProcesadas(exitos)
                .matriculasNoProcesadas(fallidos)
                .build();
    }

    @Override
    public MatriculaEstudianteCursosResponse matriculaEstudianteCursos(MatriculaEstudianteCursosRequest request) {
        if (request == null || request.getEstudianteId() == null) {
            throw new IllegalArgumentException("La solicitud de matrÃ­cula es requerida");
        }
        // Validar periodo de matrÃ­cula
        validarPeriodoMatricula();

        Estudiante estudiante =matriculaRepository.getEstudianteByIdAndEstado(request.getEstudianteId(), EstadoEstudianteMaestria.ACTIVO)
                .orElseThrow(() -> new EntityNotFoundException("No estÃ¡ activo o no existe el estudiante con ID: " + request.getEstudianteId()));

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
                if (matricula.getEstadoMatricula().equals(MatriculaEstado.CANCELADA.name()) ||
                    matricula.getEstadoMatricula().equals(MatriculaEstado.APROBADA.name()) ||
                    matricula.getEstadoMatricula().equals(MatriculaEstado.RECHAZADA.name())) {
                    fallidos.add(MatriculaNoRealizadaResponse.builder()
                        .estudiante(modelMapper.map(estudiante, EstudianteResponse.class))
                        .curso(modelMapper.map(matricula.getCurso(), CursoResponse.class))
                        .motivo("No se puede eliminar la matrÃ­cula en estado " + matricula.getEstadoMatricula())
                        .build());
                    continue; 
                }
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
                            .motivo("El estudiante ya estÃ¡ matriculado en este curso")
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
            throw new IllegalArgumentException("La solicitud de matrÃ­cula es requerida");
        }
        // Validar periodo de matrÃ­cula
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
                if (matricula.getEstadoMatricula().equals(MatriculaEstado.CANCELADA.name()) ||
                    matricula.getEstadoMatricula().equals(MatriculaEstado.APROBADA.name()) ||
                    matricula.getEstadoMatricula().equals(MatriculaEstado.RECHAZADA.name())) {
                    fallidos.add(MatriculaNoRealizadaResponse.builder()
                        .estudiante(modelMapper.map(matricula.getEstudiante(), EstudianteResponse.class))
                        .curso(modelMapper.map(matricula.getCurso(), CursoResponse.class))
                        .motivo("No se puede eliminar la matrÃ­cula en estado " + matricula.getEstadoMatricula())
                        .build());
                    continue; 
                }

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
                            .motivo("El estudiante ya estÃ¡ matriculado en este curso")
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
            throw new IllegalArgumentException("Los parÃ¡metros estudianteId y cursoId son requeridos");
        }
        // Validar que el estudiante estÃ© activo
        matriculaRepository.getEstudianteByIdAndEstado(estudianteId,EstadoEstudianteMaestria.ACTIVO)
                .orElseThrow(() -> new EntityNotFoundException("No estÃ¡ activo o no existe el estudiante con ID: " + estudianteId));

        // Validar periodo de matrÃ­cula
        validarPeriodoMatricula();
        
        // Obtener y validar el curso
        Curso curso = validarYObtenerCurso(cursoId);
        
        // Validar prerequisitos de la asignatura
        validarPrerequisitos(estudianteId, curso.getAsignatura().getId());
        
        // Obtener periodo acadÃ©mico activo
        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo acadÃ©mico activo"));
        
        // Verificar si el estudiante ya estÃ¡ matriculado en esta asignatura en este periodo
        boolean yaMatriculado = matriculaRepository.existsMatriculaByEstudianteIdAndPeriodoIdAndAsignaturaId(
                estudianteId, periodoActivo.getId(), curso.getAsignatura().getId());

        
        if (yaMatriculado) {
            throw new IllegalArgumentException("El estudiante ya estÃ¡ matriculado en esta asignatura para el periodo actual");
        }
        
        // Verificar si el estudiante ya ganÃ³ la asignatura
        boolean asignaturaGanada = matriculaRepository.asignaturaGanada(estudianteId, curso.getAsignatura().getId());
        if (asignaturaGanada) {
            throw new IllegalArgumentException("El estudiante ya ganÃ³ esta asignatura");
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
        
        // Obtener matrÃ­culas para cada estudiante especificado
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

        // Validar periodo de matrÃ­cula
        validarPeriodoMatricula();
        
        // Obtener periodo acadÃ©mico activo
        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo acadÃ©mico activo"));
        
        // Obtener todas las asignaturas activas
        List<Asignatura> todasLasAsignaturas = cursoRepository.findAsignaturasByStatus(true,null);
        
        // Obtener asignaturas ya matriculadas por el estudiante en el periodo actual
        List<Asignatura> asignaturasMatriculadas = matriculaRepository.getAsignaturasMatriculadas(estudianteId, periodoActivo.getId());
        
        // Filtrar asignaturas disponibles
        List<Asignatura> asignaturasDisponibles = todasLasAsignaturas.stream()
                .filter(asignatura -> {
                    // Verificar que no estÃ© ya matriculado
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
     * MÃ©todo adicional para cancelar una matrÃ­cula especÃ­fica
     */
    public String cancelarMatricula(Long matriculaId, String motivoCancelacion) {
    
        if (matriculaId == null) {
            throw new IllegalArgumentException("El ID de la matrÃ­cula es requerido");
        }
        
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new EntityNotFoundException("MatrÃ­cula no encontrada"));
            
    
        matriculaRepository.findNotaFinalByMatriculaId(matriculaId)
                .ifPresent(notaFinal -> {
                    throw new IllegalArgumentException("No se puede cancelar la matrÃ­cula, ya tiene una nota final registrada");
                });
        
        // Validar que la matrÃ­cula estÃ© activa
        if (!matricula.isEstado()) {
            throw new IllegalArgumentException("Solo se pueden cancelar matrÃ­culas activas");
        }

        // Validar periodo de matrÃ­cula
        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo acadÃ©mico activo"));
        
        LocalDate fechaActual = LocalDate.now();
        if (fechaActual.isBefore(periodoActivo.getFechaInicio())) {
            throw new IllegalArgumentException("El periodo acadÃ©mico aÃºn no ha iniciado");
        }
        if (fechaActual.isAfter(periodoActivo.getFechaFinMatricula())) {
            throw new IllegalArgumentException("El periodo de matrÃ­cula ha finalizado");
        }
        
        // Actualizar estado y observaciÃ³n
        matricula.setEstadoMatricula(MatriculaEstado.CANCELADA.name());
        matricula.setEstado(false);
        matricula.setObservacion("CANCELADA: " + motivoCancelacion);
        
        matriculaRepository.update(matricula);

        return "MatrÃ­cula cancelada exitosamente";
    }

    /**
     * MÃ©todo adicional para obtener matrÃ­culas por estudiante 
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
     * MÃ©todo adicional para obtener una matrÃ­cula por ID
     */
    public Matricula obtenerMatriculaPorId(Long matriculaId) {
        if (matriculaId == null) {
            throw new IllegalArgumentException("El ID de la matrÃ­cula es requerido");
        }
        
        return matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new EntityNotFoundException("MatrÃ­cula no encontrada con ID: " + matriculaId));
    }

    /**
     * MÃ©todo adicional para listar todas las matrÃ­culas con filtros
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
     * MÃ©todo para obtener estudiantes matriculados en un curso especÃ­fico
     */
     @Override
    public List<MatriculaCursoResponse> obtenerEstudiantesMatriculadosEnCurso(Long cursoId) {
        cursoRepository.findCursoById(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado con ID: " + cursoId));

        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo acadÃ©mico activo"));

        List<Matricula> matriculas = matriculaRepository.findByCursoIdAndPeriodoId(cursoId, periodoActivo.getId());

        return matriculas.stream()
                .map(this::toMatriculaCursoResponse)
                .collect(Collectors.toList());
    
    }


    @Override
     public MatriculaResponse cambiarEstadoMatricula(Long id, MatriculaEstadoRequest request) {
      
        Matricula matricula = matriculaRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("MatrÃ­cula no encontrada"));

        if (request.getEstado().equalsIgnoreCase(MatriculaEstado.APROBADA.name()) ||
            request.getEstado().equalsIgnoreCase(MatriculaEstado.RECHAZADA.name()) ||
            request.getEstado().equalsIgnoreCase(MatriculaEstado.CREADA.name()) ||
            request.getEstado().equalsIgnoreCase(MatriculaEstado.TUTOR_AVALADA.name()) ||
            request.getEstado().equalsIgnoreCase(MatriculaEstado.TUTOR_NO_AVALADA.name()) ||
            request.getEstado().equalsIgnoreCase(MatriculaEstado.CANCELADA.name())) {
            
            matricula.setEstadoMatricula(request.getEstado().toUpperCase());
            if (request.getEstado().equalsIgnoreCase(MatriculaEstado.CANCELADA.name()) ||
            request.getEstado().equalsIgnoreCase(MatriculaEstado.RECHAZADA.name()) )
            {
                matricula.setEstado(false);
            }
            matriculaRepository.update(matricula);
            return modelMapper.map(matricula, MatriculaResponse.class);
            
        }else {
            throw new IllegalArgumentException("Estado de matrÃ­cula no vÃ¡lido");
        }


     }


    /**
     * MÃ©todo auxiliar para validar que el periodo acadÃ©mico estÃ© activo y dentro del plazo de matrÃ­cula
     */
    private void validarPeriodoMatricula() {
        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo acadÃ©mico activo"));
        
        LocalDate fechaActual = LocalDate.now();
        
        if (fechaActual.isBefore(periodoActivo.getFechaInicio())) {
            throw new IllegalArgumentException("El periodo acadÃ©mico aÃºn no ha iniciado");
        }
        
        if (fechaActual.isAfter(periodoActivo.getFechaFinMatricula())) {
            throw new IllegalArgumentException("El periodo de matrÃ­cula ha finalizado");
        }
    }

    /**
     * MÃ©todo auxiliar para validar que un curso existe y estÃ¡ disponible para matrÃ­cula
     * @param  cursoId
     */
    private Curso validarYObtenerCurso(Long cursoId) {
        Curso curso = cursoRepository.findCursoById(cursoId)
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado con ID: " + cursoId));
        
        // Verificar que el curso pertenezca al periodo activo
        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo acadÃ©mico activo"));
        
        if (!curso.getPeriodo().getId().equals(periodoActivo.getId())) {
            throw new IllegalArgumentException("El curso no pertenece al periodo acadÃ©mico activo");
        }

        if (!curso.isEstado()) {
            throw new IllegalArgumentException("El curso no estÃ¡ disponible para matrÃ­cula");
        }

        return curso;
    }

    /**
     * MÃ©todo auxiliar para crear una matrÃ­cula
     */
    private Matricula crearMatricula(Long estudianteId, Curso curso, String observacion) {
        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo acadÃ©mico activo"));
        Estudiante estudiante= matriculaRepository.getEstudianteById(estudianteId)
                .orElseThrow(() -> new EntityNotFoundException("Estudiante no encontrado con ID: " + estudianteId));
        return Matricula.builder()
                .estudiante(estudiante)
                .curso(curso)
                .periodo(periodoActivo)
                .estado(true)
                .estadoMatricula(MatriculaEstado.CREADA.name())
                .observacion(observacion)
                .build();
    }

    /**
     * Validar prerequisitos de una asignatura para un estudiante antes de la matrÃ­cula.
     * (Actualmente sÃ³lo valida estado de la asignatura; extender si hay tabla de prerequisitos)
     */
    private void validarPrerequisitos(Long estudianteId, Long asignaturaId) {
        
        
        Asignatura asignatura = cursoRepository.findAsignaturaById(asignaturaId)
                .orElseThrow(() -> new EntityNotFoundException("Asignatura no encontrada"));
        
        if (!asignatura.getEstado()) {
            throw new IllegalArgumentException("La asignatura no estÃ¡ disponible para matrÃ­cula");
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

     

   
	



    @Override
    public List<TutorNotificacionResponse> notificarMatriculasAprobadas(ListEstudianteRequest request) {
        if (request == null || request.getEstudianteIds() == null || 
            request.getEstudianteIds().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos un estudiante para la notificacion");
        }

        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo academico activo"));
        List<TutorNotificacionResponse> notificaciones = new ArrayList<>();
        Set<String> correosEnviados = new HashSet<>();

        for (Long estudianteId : request.getEstudianteIds()) {
            if (estudianteId == null) {
                continue;
            }

            Estudiante estudiante = matriculaRepository.getEstudianteById(estudianteId).orElse(null);
            if (estudiante == null) {
                LOGGER.warn("Estudiante {} no encontrado, se omite notificacion", estudianteId);
                continue;
            }

            List<Matricula> matriculas = matriculaRepository.findByEstudianteIdAndPeriodoActivo(estudianteId);
            List<Matricula> aprobadas = matriculas.stream()
                    .filter(this::esMatriculaAprobada)
                    .toList();

            if (aprobadas.isEmpty()) {
                LOGGER.info("Estudiante {} sin matriculas aprobadas en periodo activo", estudianteId);
                continue;
            }

            byte[] reporte = generarReporteMatricula(estudiante, aprobadas, periodoActivo);
            int totalAprobadas = aprobadas.size();
            String asunto = "Matricula final aprobada";

            String correoEstudiante = resolveCorreoEstudiante(estudiante);
            if (correoEstudiante != null && !correoEstudiante.isBlank()) {
                String normalized = correoEstudiante.trim().toLowerCase();
                if (correosEnviados.add(normalized)) {
                    String cuerpo = emailService.buildCorreoHtml("Reporte de Matrícula", buildCuerpoCorreoEstudiante(estudiante, periodoActivo, totalAprobadas));
                    sendEmailWithAttachmentSafe(correoEstudiante, asunto, cuerpo, reporte,
                            buildNombreArchivoReporte(estudiante), "application/pdf");
                    notificaciones.add(TutorNotificacionResponse.builder()
                            .tutorId(estudiante.getId())
                            .nombre(buildNombrePersona(estudiante.getPersona()))
                            .codigo(estudiante.getCodigo())
                            .correo(correoEstudiante)
                            .totalEstudiantesConMatriculaActiva(totalAprobadas)
                            .build());
                }
            } else {
                LOGGER.warn("Estudiante {} sin correo, se omite notificacion", estudianteId);
            }

            List<Docente> tutores = estudianteDocenteRepository.findTutoresByEstudiante(estudianteId);
            for (Docente tutor : tutores) {
                String correoTutor = resolveCorreoDocente(tutor);
                if (correoTutor == null || correoTutor.isBlank()) {
                    LOGGER.warn("Tutor {} sin correo, se omite notificacion", tutor != null ? tutor.getId() : null);
                    continue;
                }
                String normalized = correoTutor.trim().toLowerCase();
                if (!correosEnviados.add(normalized)) {
                    continue;
                }
                String cuerpo = emailService.buildCorreoHtml("Reporte de Matrícula", buildCuerpoCorreoTutor(tutor, estudiante, periodoActivo, totalAprobadas));
                sendEmailWithAttachmentSafe(correoTutor, asunto, cuerpo, reporte,
                        buildNombreArchivoReporte(estudiante), "application/pdf");
                notificaciones.add(TutorNotificacionResponse.builder()
                        .tutorId(tutor != null ? tutor.getId() : null)
                        .nombre(buildNombrePersona(tutor != null ? tutor.getPersona() : null))
                        .codigo(tutor != null ? tutor.getCodigo() : "")
                        .correo(correoTutor)
                        .totalEstudiantesConMatriculaActiva(1)
                        .build());
            }
        }

        return notificaciones;
    }

    private boolean esMatriculaAprobada(Matricula matricula) {
        return matricula != null
                && matricula.getEstadoMatricula() != null
                && MatriculaEstado.APROBADA.name().equalsIgnoreCase(matricula.getEstadoMatricula());
    }

    private void sendEmailWithAttachmentSafe(String to, String subject, String body, byte[] attachment,
            String attachmentName, String contentType) {
        try {
            emailService.sendEmailWithAttachment(to, subject, body, attachment, attachmentName, contentType)
                    .exceptionally(ex -> {
                        LOGGER.error("Error enviando correo a {}", to, ex);
                        return null;
                    });
        } catch (Exception ex) {
            LOGGER.error("Error enviando correo a {}", to, ex);
        }
    }

    private byte[] generarReporteMatricula(Estudiante estudiante, List<Matricula> matriculas, PeriodoAcademico periodo) {
        List<ReportCursoDto> data = matriculas.stream()
                .map(this::toReportCursoDto)
                .toList();
        try (InputStream reportStream = getClass().getResourceAsStream("/Reportes/Matricula.jasper");
             InputStream logoStream = getClass().getResourceAsStream("/image/logo-unicauca.png")) {
            if (reportStream == null) {
                throw new IllegalArgumentException("No se encontro el reporte Matricula.jasper");
            }
            if (logoStream == null) {
                throw new IllegalArgumentException("No se encontro el logo para el reporte");
            }
            Map<String, Object> params = new HashMap<>();
            params.put("logoUnicauca", new BufferedInputStream(logoStream));
            params.put("fecha_periodo", periodo.getFechaInicio() + " - " + periodo.getFechaFin());
            params.put("codigo", estudiante.getCodigo() != null ? estudiante.getCodigo() : "");
            params.put("identificacion", resolveIdentificacion(estudiante));
            params.put("nombre_estudiante", buildNombrePersona(estudiante.getPersona()));
            params.put("semestre", resolveSemestre(estudiante));
            params.put("ds", new JRBeanArrayDataSource(data.toArray()));
            JasperPrint print = JasperFillManager.fillReport(reportStream, params,
                    new JRBeanArrayDataSource(data.toArray()));
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException e) {
            throw new IllegalStateException("Error generando el reporte de matricula", e);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el reporte de matricula", e);
        }
    }

    private ReportCursoDto toReportCursoDto(Matricula matricula) {
        Curso curso = matricula != null ? matricula.getCurso() : null;
        return ReportCursoDto.builder()
                .grupo(curso != null && curso.getGrupo() != null ? curso.getGrupo() : "")
                .asignatura(curso != null && curso.getAsignatura() != null && curso.getAsignatura().getNombre() != null
                        ? curso.getAsignatura().getNombre()
                        : "")
                .docentes(curso != null ? formatDocentes(curso.getDocentes()) : "")
                .horario(curso != null && curso.getHorario() != null ? curso.getHorario() : "")
                .salon(curso != null && curso.getSalon() != null ? curso.getSalon() : "")
                .build();
    }

    private String formatDocentes(List<Docente> docentes) {
        if (docentes == null || docentes.isEmpty()) {
            return "Sin docentes";
        }
        return docentes.stream()
                .map(this::formatDocenteNombre)
                .collect(Collectors.joining(", "));
    }

    private String formatDocenteNombre(Docente docente) {
        if (docente == null) {
            return "";
        }
        if (docente.getPersona() == null) {
            return docente.getCodigo() != null ? docente.getCodigo() : "";
        }
        String nombre = docente.getPersona().getNombre() != null ? docente.getPersona().getNombre() : "";
        String apellido = docente.getPersona().getApellido() != null ? docente.getPersona().getApellido() : "";
        String full = (nombre + " " + apellido).trim();
        return full.isEmpty() ? (docente.getCodigo() != null ? docente.getCodigo() : "") : full;
    }

    private String resolveCorreoEstudiante(Estudiante estudiante) {
        if (estudiante == null) {
            return null;
        }
        if (estudiante.getCorreoUniversidad() != null && !estudiante.getCorreoUniversidad().isBlank()) {
            return estudiante.getCorreoUniversidad();
        }
        if (estudiante.getPersona() != null && estudiante.getPersona().getCorreoElectronico() != null
                && !estudiante.getPersona().getCorreoElectronico().isBlank()) {
            return estudiante.getPersona().getCorreoElectronico();
        }
        return null;
    }

    private String resolveCorreoDocente(Docente docente) {
        if (docente == null || docente.getPersona() == null) {
            return null;
        }
        return docente.getPersona().getCorreoElectronico();
    }

    private String buildCuerpoCorreoEstudiante(Estudiante estudiante, PeriodoAcademico periodo, int totalAprobadas) {
        String nombre = buildNombrePersona(estudiante.getPersona());
        String saludo = nombre.isEmpty() ? "Cordial saludo," : "Cordial saludo, " + nombre + ".";
        String cuerpo = "<p>" + saludo + "</p>"
        + "<p>Te informamos que tu matrícula correspondiente al periodo "
        + periodo.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yy")) + " - " + periodo.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yy")) + " ha sido procesada exitosamente.</p>"
        + "<p>Adjuntamos el reporte con las materias matriculadas para dicho periodo.</p>"
        + "<p>Atentamente,</p>"
                + "<p><strong>Maestría en Computación</strong></p>";
        return cuerpo;
    }

    private String buildCuerpoCorreoTutor(Docente tutor, Estudiante estudiante, PeriodoAcademico periodo,
            int totalAprobadas) {
        String nombreTutor = buildNombrePersona(tutor != null ? tutor.getPersona() : null);
        String saludo = nombreTutor.isEmpty() ? "Cordial saludo," : "Cordial saludo, " + nombreTutor + ".";
        String nombreEstudiante = buildNombrePersona(estudiante != null ? estudiante.getPersona() : null);
        String codigo = estudiante != null && estudiante.getCodigo() != null ? estudiante.getCodigo() : "";
        String estudianteLabel = nombreEstudiante.isEmpty() ? codigo : nombreEstudiante + (codigo.isEmpty() ? "" : " (" + codigo + ")");
        String cuerpo = "<p>" + saludo + "</p>"
                + "<p>Se aprobo la matricula final del estudiante <strong>" + estudianteLabel + "</strong>.</p>"
                + "<p>Adjuntamos el reporte con <strong>" + totalAprobadas + "</strong> cursos del periodo "
                + periodo.getTagPeriodo() + ".</p>"
                + "<p>Universitariamente,</p>"
                + "<p><strong>Universidad del Cauca</strong></p>";
        return  cuerpo;
    }

   

    private String buildNombrePersona(Persona persona) {
        if (persona == null) {
            return "";
        }
        String nombre = persona.getNombre() != null ? persona.getNombre() : "";
        String apellido = persona.getApellido() != null ? persona.getApellido() : "";
        return (nombre + " " + apellido).trim();
    }

    private String resolveIdentificacion(Estudiante estudiante) {
        if (estudiante == null || estudiante.getPersona() == null || estudiante.getPersona().getIdentificacion() == null) {
            return "";
        }
        return estudiante.getPersona().getIdentificacion().toString();
    }

    private String resolveSemestre(Estudiante estudiante) {
        if (estudiante == null || estudiante.getInformacionMaestria() == null) {
            return "";
        }
        Integer semestre = estudiante.getInformacionMaestria().getSemestreAcademico();
        if (semestre == null) {
            semestre = estudiante.getInformacionMaestria().getSemestreFinanciero();
        }
        return semestre != null ? semestre.toString() : "";
    }

    private String buildNombreArchivoReporte(Estudiante estudiante) {
        String codigo = estudiante != null && estudiante.getCodigo() != null ? estudiante.getCodigo() : "estudiante";
        return "matricula_final_" + codigo + ".pdf";
    }

}
