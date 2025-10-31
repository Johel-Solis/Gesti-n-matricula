package unicauca.edu.co.ms_gestion_maticula.app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.AreaFormacionEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AreaFormacion {
    private Long id;
    private String nombre;
    private String descripcion;


    public AreaFormacionEntity toEntity(){
        AreaFormacionEntity entity = AreaFormacionEntity.builder()
                .idArea(this.id)
                .nombreArea(this.nombre)
                .descripcion(this.descripcion)
                .build();
        return entity;
    }

    public static AreaFormacion fromEntity(AreaFormacionEntity entity){
        return AreaFormacion.builder()
                .id(entity.getIdArea())
                .nombre(entity.getNombreArea())
                .descripcion(entity.getDescripcion())
                .build();
    }
    
}
