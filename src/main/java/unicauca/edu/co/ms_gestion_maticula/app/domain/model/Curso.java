package unicauca.edu.co.ms_gestion_maticula.app.domain.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private PeriodoAcademico periodo; 
    private Asignatura asignatura; 
    private List<Docente> docentes; 
    private List<MaterialApoyo> materiales;
    private String horario;
    private String salon;
    private String observacion;
    

    public CursoEntity toEntity(){
        if(docentes == null){
            docentes = new ArrayList<>();
        }
        if(materiales == null){
            materiales = new ArrayList<>();
        }
    return CursoEntity.builder()
                .id(this.id)
                .grupo(this.grupo)
                .periodo(periodo.toEntity())
                .asignatura(this.asignatura.toEntity())
                .docentes(this.docentes.stream()
                        .map(docente -> docente.toEntity())
                        .collect(Collectors.toList()))
                .materiales(this.materiales.stream()
                        .map(MaterialApoyo::toEntity)
                        .collect(Collectors.toList()))
                .horario(this.horario)
                .salon(this.salon)
                .observacion(this.observacion)
                .build();
    }
}
