package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "docentes_asignaturas", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id_docente", "id_asignatura"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocenteAsignaturaEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_docente")
    private DocenteEntity docente;

    @ManyToOne
    @JoinColumn(name = "id_asignatura")
    @JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "docentesAsignaturas" })
    private AsignaturaEntity asignatura;

    @Column(name = "dicta_asignatura")
    private Boolean dictaAsignatura;

}
