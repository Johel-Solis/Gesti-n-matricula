package unicauca.edu.co.ms_gestion_maticula.domain.response;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportEstudianteCursoDto {

  private String codigoEstudiante;
  private String nombreEstudiante;
  private String identificacion;
  private String correoEstudiante;
  private String semestre;
  private String totalMatricula;
  private List<ReportCursoDto> cursos;

}
