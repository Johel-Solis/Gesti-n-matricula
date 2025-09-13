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
    private Long periodoId;
    private String periodoDescripcion;
    private Long asignaturaId;
    private String asignaturaNombre;
    private List<DocenteResumen> docentes;
    private String horario;
    private String salon;
    private String observacion;

    @Data @AllArgsConstructor
    public static class DocenteResumen {
        private Long id;
        private String nombre; 
    }
}
