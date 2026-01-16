package unicauca.edu.co.ms_gestion_maticula.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.TutorDto;

@Data 
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TutorEstudiante {

    private Docente docente;
    private Long totalEstudiantes;

    public TutorEstudiante fromEntity( TutorDto entity){
        return TutorEstudiante.builder()
        .docente(entity.getDocente().toDomain())
        .totalEstudiantes(entity.getTotalEstudiantes())
        .build();
    }
}
