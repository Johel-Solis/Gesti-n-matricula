package unicauca.edu.co.ms_gestion_maticula.domain.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaCursoEstudiantesRequests {

    @NotEmpty(message = "{matriculaCursoEstudiantes.matriculaEstudianteCursos.notempty}")
    private List<MatriculaEstudianteCursosRequest> matriculaEstudianteCursos;

}
