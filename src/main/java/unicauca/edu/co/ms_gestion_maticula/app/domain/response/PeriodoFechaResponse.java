package unicauca.edu.co.ms_gestion_maticula.app.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PeriodoFechaResponse {
    private Boolean disponible;
    private String mensaje;

}
