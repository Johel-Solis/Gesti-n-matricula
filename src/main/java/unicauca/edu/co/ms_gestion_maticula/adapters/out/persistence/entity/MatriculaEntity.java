package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity;

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
@Table(name = "matriculas")
@Data 
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatriculaEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "id_estudiante")
    private Long estudianteId;
    
    @ManyToOne
    @JoinColumn(name = "id_curso")  
    private CursoEntity curso;
    
    @ManyToOne
    @JoinColumn(name = "id_periodo")   
    private PeriodoAcademicoEntity periodo;
    private String estado;
    private String observacion;



}
