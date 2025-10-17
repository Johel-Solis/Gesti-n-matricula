package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Asignatura;

@Entity
@Table(name = "asignaturas")
@Data 
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AsignaturaEntity {
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAsignatura;

    @Column(name = "codigo_asignatura", unique = true)
    private Long codigoAsignatura;

    @Column(name = "nombre_asignatura", unique = true)
    private String nombreAsignatura;

    @Column(name = "estado_asignatura")
    private Boolean estadoAsignatura;

    @Column(name = "area_formacion")
    private Integer areaFormacion;

    @Column(name = "tipo_asignatura")
    private String tipoAsignatura;

    @Column(name = "creditos")
    private Integer creditos;

    @Transient
    private List<DocenteEntity> listaDocentes;


    @OneToMany(mappedBy = "asignatura", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "asignatura" })
    private List<DocenteAsignaturaEntity> docentesAsignaturas;

    public Asignatura toDomain() {
        return Asignatura.builder()
                .id(this.idAsignatura)
                .codigo(this.codigoAsignatura)
                .nombre(this.nombreAsignatura)
                .estado(this.estadoAsignatura)
                .areaFormacion(this.areaFormacion)
                .tipo(this.tipoAsignatura)
                .creditos(this.creditos)
                .build();
    }
   
}
