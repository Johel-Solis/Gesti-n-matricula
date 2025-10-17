package unicauca.edu.co.ms_gestion_maticula.app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.PersonaEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Persona {
    private Long id;
    private Long identificacion; 
    private String nombre;
    private String apellido;
    private String correoElectronico;
    private String telefono;
    private String genero;
    private String tipoIdentificacion;

    public PersonaEntity toEntity(){
        PersonaEntity entity = new PersonaEntity();
        entity.setId(this.id);
        entity.setIdentificacion(this.identificacion);
        entity.setNombre(this.nombre);
        entity.setApellido(this.apellido);
        entity.setCorreoElectronico(this.correoElectronico);
        entity.setTelefono(this.telefono);
        entity.setGenero(this.genero);
        entity.setTipoIdentificacion(this.tipoIdentificacion);
        return entity;
    }

    public static Persona fromEntity(PersonaEntity entity){
        if(entity == null) return null;
        return Persona.builder()
                .id(entity.getId())
                .identificacion(entity.getIdentificacion())
                .nombre(entity.getNombre())
                .apellido(entity.getApellido())
                .correoElectronico(entity.getCorreoElectronico())
                .telefono(entity.getTelefono())
                .genero(entity.getGenero())
                .tipoIdentificacion(entity.getTipoIdentificacion())
                .build();
    }
}
