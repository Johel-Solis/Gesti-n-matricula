package unicauca.edu.co.ms_gestion_maticula.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Persona;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocenteResponse {
    private Long id;
    private Persona persona;
    private String codigo;
    private String facultad;
    private String departamento;
}
