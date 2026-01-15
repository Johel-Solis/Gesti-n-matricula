package unicauca.edu.co.ms_gestion_maticula.domain.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoMatriculaEstudiantesRequest {

    @NotNull(message = "{cursoMatricula.cursoId.notnull}")
    private Long cursoId;
    private List<EstudianteMatriculaRequest> estudiantes;
}
