package unicauca.edu.co.ms_gestion_maticula.app.web.controller.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Matricula;
import unicauca.edu.co.ms_gestion_maticula.domain.request.MatriculaCursoEstudiantesRequests;
import unicauca.edu.co.ms_gestion_maticula.domain.request.MatriculaEstudianteCursosRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.response.AsignaturaResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MatriculaResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.service.service.MatriculaServiceImpl;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.utils.ApiResponse;

@RestController
@RequestMapping("/api/matriculas")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaServiceImpl matriculaService;
    private final ModelMapper modelMapper;

    /**
     * Endpoint para matricular múltiples estudiantes en cursos
     */
    @PostMapping("/batch")
    public ResponseEntity<ApiResponse> matricularEstudiantesEnCursos(
            @Validated @RequestBody MatriculaCursoEstudiantesRequests requests) {
        matriculaService.matricularEstudiantesEnCursos(requests);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Matrículas realizadas exitosamente", null, 201));
    }

    /**
     * Endpoint para matricular un estudiante en múltiples cursos
     */
    @PostMapping("/estudiante")
    public ResponseEntity<ApiResponse> matricularEstudianteCursos(
            @Validated @RequestBody MatriculaEstudianteCursosRequest request) {
        matriculaService.matriculaEstudianteCursos(request);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Matrícula del estudiante realizada exitosamente", null, 201));
    }

    /**
     * Endpoint para matricular estudiantes en un curso específico
     */
    @PostMapping("/curso")
    public ResponseEntity<ApiResponse> matricularCursoEstudiantes(
            @Validated @RequestBody MatriculaCursoEstudiantesRequests requests) {
        matriculaService.matricularCursoEstudiantes(requests);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Matrículas en curso realizadas exitosamente", null, 201));
    }

    /**
     * Endpoint para validar si un estudiante puede matricularse en un curso
     */
    @GetMapping("/validar")
    public ResponseEntity<ApiResponse> validarMatriculaEstudiante(
            @RequestParam Long estudianteId, 
            @RequestParam Long cursoId) {
        Boolean esValida = matriculaService.validarMatriculaEstudiantes(estudianteId, cursoId);
        String mensaje = esValida ? "El estudiante puede matricularse en el curso" : "El estudiante no puede matricularse en el curso";
        return ResponseEntity.ok(new ApiResponse("SUCCESS", mensaje, esValida, 200));
    }

    /**
     * Endpoint para consultar matrículas de estudiantes
     */
    @PostMapping("/consultar")
    public ResponseEntity<ApiResponse> consultarMatriculaEstudiantes(
            @Validated @RequestBody MatriculaCursoEstudiantesRequests requests) {
        List<Matricula> matriculas = matriculaService.consultarMatriculaEstudiantes(requests);
        List<MatriculaResponse> matriculasResponse = matriculas.stream()
                .map(matricula -> modelMapper.map(matricula, MatriculaResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Matrículas encontradas", matriculasResponse, 200));
    }

    /**
     * Endpoint para obtener matrículas de un estudiante específico
     */
    @GetMapping("/estudiante/{estudianteId}")
    public ResponseEntity<ApiResponse> obtenerMatriculasPorEstudiante(@PathVariable Long estudianteId) {
        List<Matricula> matriculas = matriculaService.obtenerMatriculasPorEstudianteYPeriodo(estudianteId, null);
        List<MatriculaResponse> matriculasResponse = matriculas.stream()
                .map(matricula -> modelMapper.map(matricula, MatriculaResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Matrículas del estudiante encontradas", matriculasResponse, 200));
    }

    /**
     * Endpoint para obtener matrículas de un estudiante en un periodo específico
     */
    @GetMapping("/estudiante/{estudianteId}/periodo/{periodoId}")
    public ResponseEntity<ApiResponse> obtenerMatriculasPorEstudianteYPeriodo(
            @PathVariable Long estudianteId, 
            @PathVariable Long periodoId) {
        List<Matricula> matriculas = matriculaService.obtenerMatriculasPorEstudianteYPeriodo(estudianteId, periodoId);
        List<MatriculaResponse> matriculasResponse = matriculas.stream()
                .map(matricula -> modelMapper.map(matricula, MatriculaResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Matrículas encontradas", matriculasResponse, 200));
    }

    /**
     * Endpoint para obtener asignaturas disponibles para un estudiante
     */
    @GetMapping("/asignaturas-disponibles/{estudianteId}")
    public ResponseEntity<ApiResponse> obtenerAsignaturasDisponibles(@PathVariable Long estudianteId) {
        List<Asignatura> asignaturas = matriculaService.obtenerAsignaturasDisponiblesporEstudiante(estudianteId);
        List<AsignaturaResponse> asignaturasResponse = asignaturas.stream()
                .map(asignatura -> modelMapper.map(asignatura, AsignaturaResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Asignaturas disponibles encontradas", asignaturasResponse, 200));
    }

    /**
     * Endpoint para cancelar una matrícula específica
     */
    @PutMapping("/{matriculaId}/cancelar")
    public ResponseEntity<ApiResponse> cancelarMatricula(
            @PathVariable Long matriculaId,
            @RequestParam String motivo) {
        matriculaService.cancelarMatricula(matriculaId, motivo);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Matrícula cancelada exitosamente", null, 200));
    }

    /**
     * Endpoint para obtener una matrícula específica por ID
     */
    @GetMapping("/{matriculaId}")
    public ResponseEntity<ApiResponse> obtenerMatriculaPorId(@PathVariable Long matriculaId) {
        Matricula matricula = matriculaService.obtenerMatriculaPorId(matriculaId);
        MatriculaResponse matriculaResponse = modelMapper.map(matricula, MatriculaResponse.class);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Matrícula encontrada", matriculaResponse, 200));
    }

    /**
     * Endpoint para listar todas las matrículas con filtros opcionales
     */
    @GetMapping
    public ResponseEntity<ApiResponse> listarMatriculas(
            @RequestParam(required = false) Long periodoId,
            @RequestParam(required = false) String estado) {
        List<Matricula> matriculas = matriculaService.listarMatriculas(periodoId, estado);
        List<MatriculaResponse> matriculasResponse = matriculas.stream()
                .map(matricula -> modelMapper.map(matricula, MatriculaResponse.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Matrículas encontradas", matriculasResponse, 200));
    }

    /**
     * Endpoint para obtener estadísticas de matrícula
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<ApiResponse> obtenerEstadisticasMatricula(
            @RequestParam(required = false) Long periodoId) {
        try {
            // Este endpoint requiere implementación adicional en el servicio
            return ResponseEntity.ok(new ApiResponse("INFO", 
                    "Funcionalidad de estadísticas en desarrollo", null, 200));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse("ERROR", "Error al obtener estadísticas", null, 400));
        }
    }
}
