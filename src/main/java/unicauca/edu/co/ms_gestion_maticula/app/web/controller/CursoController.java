package unicauca.edu.co.ms_gestion_maticula.app.web.controller;

import java.util.List;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import unicauca.edu.co.ms_gestion_maticula.domain.model.AreaFormacion;
import unicauca.edu.co.ms_gestion_maticula.domain.request.CursoReportRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.request.CursoRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.response.AsignaturaResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.CursoResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.DocenteResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.EstudianteResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.service.CursoServiceImpl;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.utils.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CursoController {

    private final CursoServiceImpl cursoService;
    public CursoController(CursoServiceImpl cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping("/existe")
    public ResponseEntity<ApiResponse> existeCurso(
            @RequestParam(required = false) String grupo,
            @RequestParam(required = false) Long asignaturaId ) {
        boolean existe = cursoService.existeCurso(grupo, asignaturaId);
        String message = existe ? "El curso ya existe" : "El curso no existe";
        return ResponseEntity.ok(new ApiResponse("SUCCESS", message, existe, 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> obtenerPorId(@PathVariable Long id) {
        CursoResponse curso = cursoService.obtenerCursoPorId(id);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Curso encontrado", curso, 200));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> listarCursos(@RequestParam(required = false) Long idArea,
            @RequestParam(required = false) Long idAsignatura,
            @RequestParam(required = false) Long idPeriodo) {
        List<CursoResponse> cursos = cursoService.obtenerTodosLosCursos(idArea, idAsignatura, idPeriodo);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Lista de cursos", cursos, 200));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> crearCurso(@Validated @RequestBody CursoRequest request) {
        CursoResponse creado = cursoService.crearCurso(request);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Curso creado", creado, 201));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> actualizarCurso(@PathVariable Long id, @Validated @RequestBody CursoRequest request) {
        CursoResponse actualizado = cursoService.actualizarCurso(id, request);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Curso actualizado", actualizado, 200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> eliminarCurso(@PathVariable Long id) {
        cursoService.eliminarCurso(id);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Curso eliminado", null, 200));
    
    }

    @GetMapping("/asignaturas/area")
    public ResponseEntity<ApiResponse> ListarAreasFormacion() {
        List<AreaFormacion> asignaturas = cursoService.obtenerAreasFormacion();
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Áreas de formación encontradas", asignaturas, 200));
    }

    @GetMapping("/asignaturas")
    public ResponseEntity<ApiResponse> ListarAsignaturas(@RequestParam(required = false) Long idArea) {
        System.out.println("ID Área: " + idArea);
        List<AsignaturaResponse> asignaturas = cursoService.obtenerAsignaturasPorEstado(idArea);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Asignaturas encontradas", asignaturas, 200));
    }

    @GetMapping("/asignaturas/docente/{id}")
    public ResponseEntity<ApiResponse> obtenerDocentePorAsignaturas(@PathVariable Long id) {
        List<DocenteResponse> docentes = cursoService.obtenerDocentesPorAsignaturaId(id);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Docentes encontrados", docentes, 200));
    }

     @GetMapping("/disponibles-estudiante/{idEstudiante}")
    public ResponseEntity<ApiResponse> cursosDisponibles(@PathVariable Long idEstudiante, @RequestParam(required = false) Long idArea) {
        List<CursoResponse> cursos = cursoService.obtenerCursosDisponibles(idEstudiante,idArea);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Lista de cursos disponibles", cursos, 200));
    }

      /**
     * 
     * Endpoint para obtener estudiantes disponibles para una asignatura
     *  
     */ 
    @GetMapping("/disponibles-estudiantes/asignatura/{asignaturaId}")
    public ResponseEntity<ApiResponse> obtenerEstudiantesDisponibles(
            @PathVariable Long asignaturaId) {
        List<EstudianteResponse> estudiantes = cursoService.obtenerEstudiantesDisponiblesPorCursoAsignatura(asignaturaId);
        
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Estudiantes disponibles encontrados", estudiantes, 200));
    }

    @GetMapping("/estudiantes/activos")
    public ResponseEntity<ApiResponse> obtenerEstudiantesActivos() {
        List<EstudianteResponse> estudiantes = cursoService.obtenerEstudiantesActivos();
        
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Estudiantes activos encontrados", estudiantes, 200));
    }


    /**
     * Endpoint para reporte de cursos ofertados
     */

    @PostMapping("/ofertados/report")
    public ResponseEntity<byte[]> generarReporteCursos(
            @RequestBody(required = false) CursoReportRequest request,
            @RequestParam(defaultValue = "pdf") String formato) {
        byte[] reporte = cursoService.generarReporteCursos(request, formato);
        boolean esExcel = formato != null && (formato.equalsIgnoreCase("xlsx") || formato.equalsIgnoreCase("excel"));
        String extension = esExcel ? "xlsx" : "pdf";
        MediaType mediaType = esExcel
                ? MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                : MediaType.APPLICATION_PDF;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDisposition(ContentDisposition.attachment().filename("cursosOfertados." + extension).build());
        return new ResponseEntity<>(reporte, headers, HttpStatus.OK);
    }
    
    @GetMapping("/matricula-aprobadas")
    public ResponseEntity<ApiResponse> getCursosAprobados() {
        List<CursoResponse> cursos = cursoService.obtenerCursosPorMatriculaAprobada();
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Lista de cursos con matricula aprobada", cursos, 200));
    }
    
    
}
