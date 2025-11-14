package unicauca.edu.co.ms_gestion_maticula.app.domain.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoMatriculaRequest {
    
    @NotNull(message = "{cursoMatricula.cursoId.notnull}")
    private Long cursoId;

    @Size(max = 200, message = "{cursoMatricula.observacion.size}")
    private String observacion;

}
