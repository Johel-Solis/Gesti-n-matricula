package unicauca.edu.co.ms_gestion_maticula.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportCursoDto {
    private String grupo;
    private String asignatura;
    private String docentes;
    private String horario;
    private String salon;
    private String creditos;
    
}
