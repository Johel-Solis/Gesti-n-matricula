package unicauca.edu.co.ms_gestion_maticula.app.domain.response;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CursoResponse {
    private Long id;
    private String grupo;
    private PeriodoAcademicoResponse periodo;
    private String periodoDescripcion;
    private AsignaturaResponse asignatura;
    private List<DocenteResponse> docentes;
    private List<MaterialApoyoResponse> materiales;
    private String horario;
    private String salon;
    private String observacion; 
}
