package unicauca.edu.co.ms_gestion_maticula.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.MaterialApoyoEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialApoyo {
    private Long id;
    private String nombre;
    private String descripcion;
    private String enlace;

    public MaterialApoyoEntity toEntity(){
        return MaterialApoyoEntity.builder()
                .id(this.id)
                .nombre(this.nombre)
                .descripcion(this.descripcion)
                .enlace(this.enlace)
                .build();
    }

}
