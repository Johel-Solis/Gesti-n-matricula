package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity;

import java.time.LocalDate;

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
import unicauca.edu.co.ms_gestion_maticula.domain.model.PeriodoAcademico;

@Entity
@Table(name = "periodo_academico")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PeriodoAcademicoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;
    @Column(name = "fecha_fin_matricula", nullable = false)
    private LocalDate fechaFinMatricula;
    @Column(name = "tag_periodo", nullable = false)
    private int tagPeriodo;
    @Column(name = "descripcion")
    private String descripcion;
    @Column(name = "estado" )
    private String estado; 


    public PeriodoAcademico toDomain() {
        return PeriodoAcademico.builder()
                .id(this.id)
                .fechaInicio(this.fechaInicio)
                .fechaFin(this.fechaFin)
                .fechaFinMatricula(this.fechaFinMatricula)
                .tagPeriodo(this.tagPeriodo)
                .descripcion(this.descripcion)
                .estado(this.estado)
                .build();
    }
}
