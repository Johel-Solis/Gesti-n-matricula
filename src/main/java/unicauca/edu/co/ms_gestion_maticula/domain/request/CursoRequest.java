package unicauca.edu.co.ms_gestion_maticula.domain.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursoRequest {

    @NotBlank(message = "{curso.grupo.notblank}")
    @Size(max = 20, message = "{curso.grupo.size}")
    private String grupo;

    
    @NotNull(message = "{curso.asignaturaId.notnull}")
    private Long asignaturaId;

    @NotEmpty(message = "{curso.docentesIds.notempty}")
    private List<@NotNull(message = "{curso.docenteId.notnull}") Long> docentesIds;

    @NotBlank(message = "{curso.horario.notblank}")
    @Size(max = 100, message = "{curso.horario.size}")
    private String horario;

    @NotBlank(message = "{curso.salon.notblank}")
    @Size(max = 50, message = "{curso.salon.size}")
    private String salon;

    private List<Long> materialApoyoIds;

    private String observacion;

    
}
