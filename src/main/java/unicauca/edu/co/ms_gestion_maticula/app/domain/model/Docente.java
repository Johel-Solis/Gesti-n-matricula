package unicauca.edu.co.ms_gestion_maticula.app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.DocenteEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Docente {
    private Long id;
    //private Long personaId;
    private Persona persona; 
    private String codigo;
    private String facultad;
    private String departamento;
    private String estado;

    public DocenteEntity toEntity(){
        return new DocenteEntity(
                this.id,
                persona.toEntity(),
                this.codigo,
                this.facultad,
                this.departamento,
                this.estado
        );
    }

    public static Docente fromEntity(DocenteEntity entity){
        if(entity == null) return null;
        return Docente.builder()
                .id(entity.getId())
                .persona(Persona.fromEntity(entity.getPersona()))
                .codigo(entity.getCodigo())
                .facultad(entity.getFacultad())
                .departamento(entity.getDepartamento())
                .estado(entity.getEstado())
                .build();
    }
}
