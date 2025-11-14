package unicauca.edu.co.ms_gestion_maticula.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatriculaResponse {
    private Long id;
    private Long estudianteId;
    private CursoResponse curso;
    private PeriodoAcademicoResponse periodo;
    private String estado;
    private String observacion;
}