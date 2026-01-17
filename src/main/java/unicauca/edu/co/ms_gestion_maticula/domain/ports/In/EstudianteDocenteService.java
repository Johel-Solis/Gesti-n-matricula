package unicauca.edu.co.ms_gestion_maticula.domain.ports.In;

import java.util.List;

import unicauca.edu.co.ms_gestion_maticula.domain.model.TutorEstudiante;
import unicauca.edu.co.ms_gestion_maticula.domain.response.EstudianteResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.TutorNotificacionResponse;


public interface EstudianteDocenteService {

    public List<TutorEstudiante> getDirectores();
    public List<EstudianteResponse> getEstudiantesByTutor(Long tutorId);
    public List<TutorNotificacionResponse> notificarTutoresConMatriculasActivas();
    
    
}
