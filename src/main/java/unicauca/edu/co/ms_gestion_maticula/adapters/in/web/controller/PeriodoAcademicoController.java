package unicauca.edu.co.ms_gestion_maticula.adapters.in.web.controller;


import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import unicauca.edu.co.ms_gestion_maticula.app.domain.request.PeriodoAcademicoRequest;
import unicauca.edu.co.ms_gestion_maticula.app.domain.request.PeriodoFechasRequest;
import unicauca.edu.co.ms_gestion_maticula.app.domain.response.PeriodoAcademicoResponse;
import unicauca.edu.co.ms_gestion_maticula.app.domain.response.PeriodoFechaResponse;
import unicauca.edu.co.ms_gestion_maticula.app.service.PeriodoAcademicoServiceImpl;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.utils.ApiResponse;

@RestController
@RequestMapping("/api/periodos")
// cors
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class PeriodoAcademicoController {
    private final PeriodoAcademicoServiceImpl useCase;

    public PeriodoAcademicoController(PeriodoAcademicoServiceImpl useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> crearPeriodo(@Valid @RequestBody PeriodoAcademicoRequest periodo) {

            System.out.println("Creando período académico: " + periodo.toString());
            PeriodoAcademicoResponse creado = useCase.crearPeriodo(periodo);
            return ResponseEntity.ok(new ApiResponse("SUCCESS", "Período creado correctamente", creado, 200));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> actualizar(@PathVariable Long id, @RequestBody PeriodoAcademicoRequest periodo) {
        PeriodoAcademicoResponse actualizado = useCase.actualizar(id, periodo);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Período actualizado", actualizado, 200));
    }

    @GetMapping
    public ResponseEntity<ApiResponse> listar() {
        List<PeriodoAcademicoResponse> periodos = useCase.listar();
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Lista de períodos", periodos, 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> obtenerPorId(@PathVariable Long id) {
        PeriodoAcademicoResponse periodo = useCase.obtenerPorId(id);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Período obtenido", periodo, 200));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> eliminar(@PathVariable Long id) {
        useCase.eliminar(id);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Período eliminado", null, 200));
    }

    @GetMapping("/validar-fechas")
    public ResponseEntity<ApiResponse> validarFechas(@Valid PeriodoFechasRequest request) {
        PeriodoFechaResponse validacion = useCase.validarFechas(request.getFechaInicio(), request.getFechaFin());
        return ResponseEntity.ok(new ApiResponse("SUCCESS", validacion.getMensaje(), validacion.getDisponible(), 200));
    }

}
