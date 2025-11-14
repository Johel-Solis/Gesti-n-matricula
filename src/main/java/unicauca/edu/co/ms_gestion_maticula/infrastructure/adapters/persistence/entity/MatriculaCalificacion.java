package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "matricula_calificaciones")
@Data 
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatriculaCalificacion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne()
    @JoinColumn(name = "id_matricula", nullable = false)
    private MatriculaEntity matricula;

    @ManyToOne()
    @JoinColumn(name = "id_asignatura", nullable = false)
    private AsignaturaEntity asignatura;
    
    @Column(name = "nota")
    private BigDecimal nota;

    @Column(name = "es_definitiva")
    private Boolean esDefinitiva;


}