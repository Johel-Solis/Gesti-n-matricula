package unicauca.edu.co.ms_gestion_maticula.domain.response;



import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Persona;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.InformacionMaestria;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstudianteResponse {

   private Long id;
   private Persona persona;
   private String codigo;
   private String ciudadResidencia;    
   private String correoUniversidad;
   private InformacionMaestria informacionMaestria;

}
