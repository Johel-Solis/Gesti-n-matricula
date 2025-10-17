package unicauca.edu.co.ms_gestion_maticula.app.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocenteResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String correoElectronico;
    private String telefono;
    private String genero;
    private String tipoIdentificacion;
    private String codigo;
    private String facultad;
    private String departamento;
}
