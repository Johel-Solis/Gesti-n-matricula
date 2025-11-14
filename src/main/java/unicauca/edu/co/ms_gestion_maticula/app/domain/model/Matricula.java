package unicauca.edu.co.ms_gestion_maticula.app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.MatriculaEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Matricula {
    private Long id;
    private Long estudianteId;
    private Curso curso;
    private PeriodoAcademico periodo;
    private String estado;
    private String observacion;

    public MatriculaEntity toEntity(){
        return MatriculaEntity.builder()
                .id(this.id)
                .estudianteId(this.estudianteId)
                .curso(this.curso.toEntity())
                .periodo(this.periodo.toEntity())
                .estado(this.estado)
                .observacion(this.observacion)
                .build();
    }

    public Matricula fromEntity(MatriculaEntity entity){
        return Matricula.builder()
                .id(entity.getId())
                .estudianteId(entity.getEstudianteId())
                .curso(entity.getCurso().toDomain())
                .periodo(entity.getPeriodo().toDomain())
                .estado(entity.getEstado())
                .observacion(entity.getObservacion())
                .build();
        }

}
