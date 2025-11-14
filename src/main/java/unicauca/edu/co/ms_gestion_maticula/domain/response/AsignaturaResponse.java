package unicauca.edu.co.ms_gestion_maticula.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AsignaturaResponse {
    private Long id;
    private String nombre;
    private String codigo;
   private Boolean estado;
    private Integer areaFormacion;
    private String tipo;
    private Integer creditos;
}
