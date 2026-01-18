package unicauca.edu.co.ms_gestion_maticula.domain.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaCancelRequest {
    private String motivo;

   
    
}
