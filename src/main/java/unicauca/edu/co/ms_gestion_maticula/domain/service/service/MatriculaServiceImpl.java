package unicauca.edu.co.ms_gestion_maticula.domain.service.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Curso;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Matricula;
import unicauca.edu.co.ms_gestion_maticula.domain.model.PeriodoAcademico;
import unicauca.edu.co.ms_gestion_maticula.domain.request.CursoMatriculaRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.request.MatriculaCursoEstudiantesRequests;
import unicauca.edu.co.ms_gestion_maticula.domain.request.MatriculaEstudianteCursosRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.ports.In.MatriculaService;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.ports.out.CursoRepository;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.ports.out.MatriculaRepository;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.ports.out.PeriodoAcademicoRepository;

@Service
@RequiredArgsConstructor
public class MatriculaServiceImpl implements MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final CursoRepository cursoRepository;
    private final PeriodoAcademicoRepository periodoAcademicoRepository;

    @Override
    public void matricularEstudiantesEnCursos(MatriculaCursoEstudiantesRequests requests) {
        if (requests == null || requests.getMatriculaEstudianteCursos() == null || 
            requests.getMatriculaEstudianteCursos().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos una solicitud de matrícula");
        }
        
        List<String> errores = new ArrayList<>();
        
        // Procesar cada solicitud de matrícula
        for (MatriculaEstudianteCursosRequest solicitud : requests.getMatriculaEstudianteCursos()) {
            try {
                matriculaEstudianteCursos(solicitud);
            } catch (Exception e) {
                errores.add("Error para estudiante " + solicitud.getEstudianteId() + ": " + e.getMessage());
            }
        }
        
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException("Errores durante las matrículas: " + String.join("; ", errores));
        }
    }

    @Override
    public void matriculaEstudianteCursos(MatriculaEstudianteCursosRequest request) {
        if (request == null || request.getEstudianteId() == null) {
            throw new IllegalArgumentException("La solicitud de matrícula es requerida");
        }
        
        if (request.getCursos() == null || request.getCursos().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos un curso para matricular");
        }
        
        // Validar periodo de matrícula
        validarPeriodoMatricula();
        
        // Validar cada curso antes de proceder con las matrículas
        for (CursoMatriculaRequest cursoRequest : request.getCursos()) {
            validarMatriculaEstudiantes(request.getEstudianteId(), cursoRequest.getCursoId());
        }
        
        // Realizar las matrículas
        List<String> errores = new ArrayList<>();
        for (CursoMatriculaRequest cursoRequest : request.getCursos()) {
            try {
                Curso curso = validarYObtenerCurso(cursoRequest.getCursoId());
                Matricula matricula = crearMatricula(request.getEstudianteId(), curso, cursoRequest.getObservacion());
                matriculaRepository.save(matricula);
                
            } catch (Exception e) {
                errores.add("Error al matricular en curso " + cursoRequest.getCursoId() + ": " + e.getMessage());
            }
        }
        
        if (!errores.isEmpty()) {
            throw new IllegalArgumentException("Errores durante la matrícula: " + String.join(", ", errores));
        }
    }

    @Override
    public void matricularCursoEstudiantes(MatriculaCursoEstudiantesRequests requests) {
        matricularEstudiantesEnCursos(requests);
    }

    @Override
    public Boolean validarMatriculaEstudiantes(Long estudianteId, Long cursoId) {
        if (estudianteId == null || cursoId == null) {
            throw new IllegalArgumentException("Los parámetros estudianteId y cursoId son requeridos");
        }

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
    public List<Matricula> consultarMatriculaEstudiantes(MatriculaCursoEstudiantesRequests requests) {
        if (requests == null || requests.getMatriculaEstudianteCursos() == null || 
            requests.getMatriculaEstudianteCursos().isEmpty()) {
            throw new IllegalArgumentException("Debe especificar al menos un estudiante para consultar");
        }
        
        List<Matricula> todasLasMatriculas = new ArrayList<>();
        
        // Obtener matrículas para cada estudiante especificado
        for (MatriculaEstudianteCursosRequest solicitud : requests.getMatriculaEstudianteCursos()) {
            if (solicitud.getEstudianteId() != null) {
                List<Matricula> matriculasEstudiante = matriculaRepository.findByEstudianteId(solicitud.getEstudianteId());
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
        if (!"ACTIVA".equals(matricula.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden cancelar matrículas activas");
        }
        
        // Validar periodo de matrícula
        validarPeriodoMatricula();
        
        // Actualizar estado y observación
        matricula.setEstado("CANCELADA");
        matricula.setObservacion(matricula.getObservacion() + " - CANCELADA: " + motivoCancelacion);
        
        matriculaRepository.update(matricula);
    }

    /**
     * Método adicional para obtener matrículas por estudiante y periodo
     */
    public List<Matricula> obtenerMatriculasPorEstudianteYPeriodo(Long estudianteId, Long periodoId) {
        if (estudianteId == null) {
            throw new IllegalArgumentException("El ID del estudiante es requerido");
        }
        
        List<Matricula> todasLasMatriculas = matriculaRepository.findByEstudianteId(estudianteId);
        
        if (periodoId != null) {
            return todasLasMatriculas.stream()
                    .filter(matricula -> matricula.getPeriodo().getId().equals(periodoId))
                    .collect(Collectors.toList());
        }
        
        return todasLasMatriculas;
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
    public List<Matricula> listarMatriculas(Long periodoId, String estado) {
        List<Matricula> todasLasMatriculas = matriculaRepository.findAll();
        
        return todasLasMatriculas.stream()
                .filter(matricula -> {
                    boolean coincidePeriodo = periodoId == null || matricula.getPeriodo().getId().equals(periodoId);
                    boolean coincideEstado = estado == null || estado.isEmpty() || matricula.getEstado().equalsIgnoreCase(estado);
                    return coincidePeriodo && coincideEstado;
                })
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
        
        return curso;
    }

    /**
     * Método auxiliar para crear una matrícula
     */
    private Matricula crearMatricula(Long estudianteId, Curso curso, String observacion) {
        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo académico activo"));
        
        return Matricula.builder()
                .estudianteId(estudianteId)
                .curso(curso)
                .periodo(periodoActivo)
                .estado("ACTIVA")
                .observacion(observacion)
                .build();
    }

    
    private void validarPrerequisitos(Long estudianteId, Long asignaturaId) {
        
        
        Asignatura asignatura = cursoRepository.findAsignaturaById(asignaturaId)
                .orElseThrow(() -> new EntityNotFoundException("Asignatura no encontrada"));
        
        if (!asignatura.getEstado()) {
            throw new IllegalArgumentException("La asignatura no está disponible para matrícula");
        }
    }

}
