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
public class MatriculaReporteCursoRequest {
    private String grupo;
    private String asignatura;
    private String docentes;
    private String horario;
    private String salon;
    private String creditos;
    private List<MatriculaReporteEstudianteRequest> estudiantes;
}
