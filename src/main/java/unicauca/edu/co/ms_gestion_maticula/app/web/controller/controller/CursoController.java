package unicauca.edu.co.ms_gestion_maticula.app.web.controller.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import unicauca.edu.co.ms_gestion_maticula.domain.model.AreaFormacion;
import unicauca.edu.co.ms_gestion_maticula.domain.request.CursoRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.response.AsignaturaResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.CursoResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.DocenteResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.service.service.CursoServiceImpl;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.utils.ApiResponse;

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

}
