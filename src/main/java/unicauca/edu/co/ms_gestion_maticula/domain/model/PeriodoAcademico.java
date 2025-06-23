package unicauca.edu.co.ms_gestion_maticula.domain.model;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.PeriodoAcademicoEntity;
import unicauca.edu.co.ms_gestion_maticula.domain.enums.PeriodoEstadoEnum;


@Data
@Builder
public class PeriodoAcademico {
    
    private Long id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private LocalDate fechaFinMatricula;
    private int tagPeriodo;
    private String descripcion;
    private String estado; 

    public PeriodoAcademico() {
     this.estado= PeriodoEstadoEnum.ACTIVO.name();
    }

    public PeriodoAcademico(Long id, LocalDate fechaInicio, LocalDate fechaFin, LocalDate fechaFinMatricula, int tagPeriodo, String descripcion, String estado) {
        this.id = id;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.fechaFinMatricula = fechaFinMatricula;
        this.tagPeriodo = tagPeriodo;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public PeriodoAcademicoEntity toEntity() {
        return PeriodoAcademicoEntity.builder()
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
