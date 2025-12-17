package unicauca.edu.co.ms_gestion_maticula.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatriculaAgrupadaResonse {
    private Long idCurso;
    private String asignatura;
    private String grupo;
    private PeriodoAcademicoResponse periodo;
    private String estado;
    private Long cantidadEstudiante;

}
