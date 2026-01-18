package unicauca.edu.co.ms_gestion_maticula.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstudianteTutorResponse {

    private EstudianteResponse estudiante;
    private int totalMatriculasPendientes;
    private int totalMatriculas;
}
