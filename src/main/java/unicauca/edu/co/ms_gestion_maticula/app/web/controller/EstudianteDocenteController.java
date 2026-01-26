package unicauca.edu.co.ms_gestion_maticula.app.web.controller;

import java.util.List;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import unicauca.edu.co.ms_gestion_maticula.domain.ports.In.EstudianteDocenteService;
import unicauca.edu.co.ms_gestion_maticula.domain.response.DocenteResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.EstudianteResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.EstudianteTutorResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.TutorNotificacionResponse;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.utils.ApiResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/api/estudiante-docente")
@CrossOrigin(origins = "http://localhost:4200", allowedHeaders = "*")
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

        List<EstudianteTutorResponse> estudiantes = estudianteDocenteService.getEstudiantesByTutor(tutorId);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Lista de estudiantes por tutor", estudiantes, 200));
    }

    @PostMapping("/tutores/notificar-prematricula")
    public ResponseEntity<ApiResponse> notificarTutoresPrematricula() {
        List<TutorNotificacionResponse> notificados = estudianteDocenteService.notificarTutoresConMatriculasActivas();
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Notificaciones enviadas", notificados, 200));
    }
    
    @GetMapping("/matriculados")
    public ResponseEntity<ApiResponse> getEstudianteMatriculados() {
        List<EstudianteResponse> param = estudianteDocenteService.getEstudiantesMatriculados();
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Lista de estudiantes matriculados", param, 200));
    }

    @GetMapping("/docente-email")
    public ResponseEntity<ApiResponse> getDocenteEmail(@RequestParam String email) {
        DocenteResponse doc = estudianteDocenteService.getDocenteByEmail(email);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Mensaje", doc, 200));
    }

    @GetMapping("/estudiante/{id}")
    public ResponseEntity<ApiResponse> getEstudianteId(@PathVariable("id") Long id) {
        EstudianteResponse estudiante = estudianteDocenteService.getEstudianteById(id);
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Estudiante encontrado", estudiante, 200));
    }

    @GetMapping("/listaDocentes")
    public ResponseEntity<ApiResponse> getDocentesActivos() {
        List<DocenteResponse> param = estudianteDocenteService.getDocentesActivos();
        return ResponseEntity.ok(new ApiResponse("SUCCESS", "Mensaje", param, 200));
    }
    
    
    
    
    
}
