package unicauca.edu.co.ms_gestion_maticula.adapters.in.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.app.domain.request.CursoRequest;
import unicauca.edu.co.ms_gestion_maticula.app.domain.response.CursoResponse;
import unicauca.edu.co.ms_gestion_maticula.app.service.CursoServiceImpl;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoServiceImpl cursoService;

    @GetMapping
    public ResponseEntity<List<CursoResponse>> buscarCursos(
            @RequestParam(required = false) String grupo,
            @RequestParam(required = false) Long periodoId,
            @RequestParam(required = false) Long asignaturaId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size) {
        List<CursoResponse> cursos = cursoService.buscarCursos(grupo, periodoId, asignaturaId, page, size);
        return ResponseEntity.ok(cursos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CursoResponse> obtenerPorId(@PathVariable Long id) {
        CursoResponse curso = cursoService.obtenerCursoPorId(id);
        return ResponseEntity.ok(curso);
    }

    @PostMapping
    public ResponseEntity<CursoResponse> crearCurso(@Validated @RequestBody CursoRequest request) {
        CursoResponse creado = cursoService.crearCurso(request);
        return ResponseEntity.ok(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CursoResponse> actualizarCurso(@PathVariable Long id, @Validated @RequestBody CursoRequest request) {
        CursoResponse actualizado = cursoService.actualizarCurso(id, request);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCurso(@PathVariable Long id) {
        cursoService.eliminarCurso(id);
        return ResponseEntity.noContent().build();
    }
}
