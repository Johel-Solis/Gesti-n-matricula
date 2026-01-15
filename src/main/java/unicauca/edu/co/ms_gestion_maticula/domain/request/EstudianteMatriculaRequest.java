package unicauca.edu.co.ms_gestion_maticula.domain.request;

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
public class EstudianteMatriculaRequest {

      @NotNull(message = "{matriculaEstudianteCursos.estudianteId.notnull}")
    private Long estudianteId;

    @Size(max = 200, message = "{cursoMatricula.observacion.size}")
    private String observacion;

}
