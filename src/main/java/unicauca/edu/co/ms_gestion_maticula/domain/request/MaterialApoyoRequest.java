package unicauca.edu.co.ms_gestion_maticula.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialApoyoRequest {

    @NotBlank(message = "{material.nombre.notblank}")
    @Size(max = 150, message = "{material.nombre.size}")
    private String nombre;

    @Size(max = 500, message = "{material.descripcion.size}")
    private String descripcion;

    @NotBlank(message = "{material.enlace.notblank}")
    @Size(max = 500, message = "{material.enlace.size}")
    private String enlace;
}
