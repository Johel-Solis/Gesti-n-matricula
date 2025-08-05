package unicauca.edu.co.ms_gestion_maticula.adapters.in.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import unicauca.edu.co.ms_gestion_maticula.app.usecases.GestionarPeriodoAcademicoUseCase;
import unicauca.edu.co.ms_gestion_maticula.domain.model.PeriodoAcademico;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.utils.ApiResponse;

@RestController
@RequestMapping("/api/periodos")
// cors
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PeriodoAcademicoController {
    private final GestionarPeriodoAcademicoUseCase useCase;

    public PeriodoAcademicoController(GestionarPeriodoAcademicoUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> crearPeriodo(@RequestBody PeriodoAcademico periodo) {
       
            PeriodoAcademico creado = useCase.crearPeriodo(periodo);
            return ResponseEntity.ok(new ApiResponse("SUCCESS", "Período creado correctamente", creado, 200));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Long id, @RequestBody PeriodoAcademico periodo) {
        var actualizado = useCase.actualizar(id, periodo);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Período actualizado", actualizado, 200));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> listar() {
        List<PeriodoAcademico> periodos = useCase.listar();
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Lista de períodos", periodos, 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> obtenerPorId(@PathVariable Long id) {
        var periodo = useCase.obtenerPorId(id);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Período obtenido", periodo, 200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Período eliminado", null, 200));
    }

    @GetMapping("/validar-fechas")
    public ResponseEntity<ApiResponse> validarFechas(
            @RequestParam("fechaInicio") String fechaInicio,
            @RequestParam("fechaFin") String fechaFin) {
        
        try {
            java.time.LocalDate inicio = java.time.LocalDate.parse(fechaInicio);
            java.time.LocalDate fin = java.time.LocalDate.parse(fechaFin);
            
            java.util.Map<String, Object> validacion = useCase.validarFechas(inicio, fin);
            
            String status = Boolean.TRUE.equals(validacion.get("disponible")) ? "SUCCESS" : "ERROR";
            return ResponseEntity.ok(new ApiResponse(status, (String) validacion.get("mensaje"), validacion.get("disponible"), 200));
            
        } catch (java.time.format.DateTimeParseException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse("ERROR", "Formato de fecha inválido. Use el formato YYYY-MM-DD", false, 400));
        }
    }

}
