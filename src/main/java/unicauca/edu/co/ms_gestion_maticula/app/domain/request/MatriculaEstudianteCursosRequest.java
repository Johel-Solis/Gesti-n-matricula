package unicauca.edu.co.ms_gestion_maticula.app.domain.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaEstudianteCursosRequest {

    @NotNull(message = "{matriculaEstudianteCursos.estudianteId.notnull}")
    private Long estudianteId;

    @NotEmpty(message = "{matriculaEstudianteCursos.cursosIds.notempty}")
    private List<CursoMatriculaRequest> cursos;
}
