package unicauca.edu.co.ms_gestion_maticula.domain.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoReportRequest {

    private List<Long> asignaturaIds;
    private List<Long> cursosIds;
}
