package unicauca.edu.co.ms_gestion_maticula.app.web.controller.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.request.MaterialApoyoRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MaterialApoyoResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.ports.In.MaterialApoyoService;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.utils.ApiResponse;

@RestController
@RequestMapping("/api/materiales")
@RequiredArgsConstructor
public class MaterialApoyoController {

    private final MaterialApoyoService service;

    @PostMapping
    public ResponseEntity<ApiResponse> crear(@Validated @RequestBody MaterialApoyoRequest request){
        MaterialApoyoResponse creado = service.crear(request);
        return ResponseEntity.ok( new ApiResponse("SUCCESS", "Material de apoyo creado", creado, 201));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Long id, @Validated @RequestBody MaterialApoyoRequest request){
        MaterialApoyoResponse actualizado = service.actualizar(id, request);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Material de apoyo actualizado", actualizado, 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> obtener(@PathVariable Long id){
        MaterialApoyoResponse encontrado = service.obtener(id);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Material de apoyo encontrado", encontrado, 200));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> listar(){
        List<MaterialApoyoResponse> materiales = service.listar();
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Lista de materiales de apoyo", materiales, 200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Long id){
        service.eliminar(id);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Material de apoyo eliminado", null, 204));
    }
}
