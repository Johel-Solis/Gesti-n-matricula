package unicauca.edu.co.ms_gestion_maticula.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstudianteMatriculaResponse {
    private Long id;
    private CursoResponse curso;
    private String estado;
    private String observacion;

}
