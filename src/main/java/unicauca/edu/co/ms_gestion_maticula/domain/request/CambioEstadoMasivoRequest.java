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
public class CambioEstadoMasivoRequest {
    private List<Long> estudiantesIds;
    private String nuevoEstado;

}
