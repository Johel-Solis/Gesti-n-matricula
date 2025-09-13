package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @Column(name = "fecha_aprobacion")
    private Date fechaAprobacion;

    @Column(name = "oficio_facultad")
    private Integer oficioFacultad;

    @Column(name = "area_formacion")
    private Integer areaFormacion;

    @Column(name = "tipo_asignatura")
    private String tipoAsignatura;

    @Column(name = "creditos")
    private Integer creditos;

    @Column(name = "objetivo_asignatura")
    private String objetivoAsignatura;

    @Column(name = "contenido_asignatura")
    private String contenidoAsignatura;

    @Column(name = "horas_presencial")
    private Integer horasPresencial;

    @Column(name = "horas_no_presencial")
    private Integer horasNoPresencial;

    @Column(name = "horas_total")
    private Integer horasTotal;

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
                .fechaAprobacion(this.fechaAprobacion)
                .oficioFacultad(this.oficioFacultad)
                .areaFormacion(this.areaFormacion)
                .tipo(this.tipoAsignatura)
                .creditos(this.creditos)
                .objetivo(this.objetivoAsignatura)
                .contenido(this.contenidoAsignatura)
                .horasPresencial(this.horasPresencial)
                .horasNoPresencial(this.horasNoPresencial)
                .horasTotal(this.horasTotal)
                .build();
    }
   
}
