package unicauca.edu.co.ms_gestion_maticula.domain.response;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaBatchResultResponse {

    @Builder.Default
    private List<MatriculaResponse> matriculasRealizadas = new ArrayList<>();

    @Builder.Default
    private List<MatriculaNoRealizadaResponse> matriculasNoRealizadas = new ArrayList<>();
}

