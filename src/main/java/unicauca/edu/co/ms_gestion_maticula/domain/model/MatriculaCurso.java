package unicauca.edu.co.ms_gestion_maticula.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.MatriculaCursoDto;

@Data 
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatriculaCurso {

    private Curso curso;
    private Long totalMatriculas;

    public MatriculaCurso fromEntity( MatriculaCursoDto entity){
        return MatriculaCurso.builder()
        .curso(entity.getCurso().toDomain())
        .totalMatriculas(entity.getTotalMatriculas())
        .build();
    }
}
