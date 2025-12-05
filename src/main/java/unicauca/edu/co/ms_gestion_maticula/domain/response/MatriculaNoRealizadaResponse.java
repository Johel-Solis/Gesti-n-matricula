package unicauca.edu.co.ms_gestion_maticula.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaNoRealizadaResponse {
    private EstudianteResponse estudiante;
    private CursoResponse curso;
    private String motivo;
}

