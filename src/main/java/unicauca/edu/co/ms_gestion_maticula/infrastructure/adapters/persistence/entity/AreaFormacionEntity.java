package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.model.AreaFormacion;

@Entity
@Table(name = "areas_formacion")
@Data 
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AreaFormacionEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idArea;

    @Column(name = "nombre")
    private String nombreArea;

    @Column(name = "descripcion")
    private String descripcion;

    public AreaFormacion toDomain() {
        return AreaFormacion.builder()
                .id(this.idArea)
                .nombre(this.nombreArea)
                .descripcion(this.descripcion)
                .build();
    }

}
