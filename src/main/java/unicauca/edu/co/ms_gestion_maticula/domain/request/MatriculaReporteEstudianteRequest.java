package unicauca.edu.co.ms_gestion_maticula.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaReporteEstudianteRequest {
    private String codigoEstudiante;
    private String nombreEstudiante;
    private String identificacion;
    private String correoEstudiante;
    private String semestre;
    private String observacion;
}
