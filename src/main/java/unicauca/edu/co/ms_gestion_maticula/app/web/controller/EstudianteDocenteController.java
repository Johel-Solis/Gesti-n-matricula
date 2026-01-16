package unicauca.edu.co.ms_gestion_maticula.app.web.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unicauca.edu.co.ms_gestion_maticula.domain.ports.In.EstudianteDocenteService;
import unicauca.edu.co.ms_gestion_maticula.domain.response.EstudianteResponse;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.utils.ApiResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/api/estudiante-docente")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EstudianteDocenteController {

    private final EstudianteDocenteService estudianteDocenteService;

    public EstudianteDocenteController(EstudianteDocenteService estudianteDocenteService) {
        this.estudianteDocenteService = estudianteDocenteService;
    }

    @GetMapping("/tutores")
    public ResponseEntity<ApiResponse> getTutores() {

        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Lista de tutores", estudianteDocenteService.getDirectores(), 200));
    }

    @GetMapping("/tutores/{id}/estudiantes")
     public ResponseEntity<ApiResponse> getEstudiantePorTutor(@PathVariable("id") Long tutorId) {

        List<EstudianteResponse> estudiantes = estudianteDocenteService.getEstudiantesByTutor(tutorId);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Lista de estudiantes por tutor", estudiantes, 200));
    }
    
    
    
}
