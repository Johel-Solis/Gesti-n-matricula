package unicauca.edu.co.ms_gestion_maticula.domain.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorNotificacionResponse {
    private Long tutorId;
    private String nombre;
    private String codigo;
    private String correo;
    private List<EstudianteResponse> estudiantes;
    private int totalEstudiantesConMatriculaActiva;
}
