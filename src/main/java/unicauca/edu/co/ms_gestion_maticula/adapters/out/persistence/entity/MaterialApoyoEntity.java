package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToMany;
import java.util.Set;
import java.util.HashSet;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.MaterialApoyo;

@Entity
@Table(name = "materiales_apoyo", uniqueConstraints = {
        @UniqueConstraint(name = "uk_material_apoyo_nombre", columnNames = {"nombrematerial"})
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MaterialApoyoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name= "nombrematerial", nullable = false, length = 150)
    private String nombre;

    @Column(name= "descripcionmaterial", length = 500)
    private String descripcion;

    @Column(name= "enlacesmaterial", nullable = false, length = 500)
    private String enlace;

    @ManyToMany(mappedBy = "materiales")
    @Builder.Default 
    private Set<CursoEntity> cursos = new HashSet<>();


    public  MaterialApoyo toDomain(){
        
        return MaterialApoyo.builder()
                .id(this.id)
                .nombre(this.nombre)
                .descripcion(this.descripcion)
                .enlace(this.enlace)
                .build();
    }
}
