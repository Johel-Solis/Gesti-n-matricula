package unicauca.edu.co.ms_gestion_maticula.app.domain.model;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.CursoEntity;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Curso {
    private Long id;
    private String grupo;
    private PeriodoAcademico periodo; // referencia por id para desacoplar del entity
    private Asignatura asignatura; // referencia por id
    private Set<Docente> docentes; // ids de docentes asignados
    private String horario;
    private String salon;
    private String observacion;

    public CursoEntity toEntity(){
        if(docentes == null){
            docentes = new HashSet<>();
        }
        return CursoEntity.builder()
                .id(this.id)
                .grupo(this.grupo)
                .periodo(periodo.toEntity())
                .asignatura(this.asignatura.toEntity())
                .docentes(this.docentes.stream()
                        .map(docente -> docente.toEntity())
                        .collect(Collectors.toSet()))
                .horario(this.horario)
                .salon(this.salon)
                .observacion(this.observacion)
                .build();
    }
}
