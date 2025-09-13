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
    private Long personaId; // referencia a persona
    private Persona persona; // objeto embebido opcional
    private String codigo;
    private String facultad;
    private String departamento;
    private String tipoVinculacion;
    private String escalafon;
    private String observacion;
    private String estado;

    public DocenteEntity toEntity(){
        return new DocenteEntity(
                this.id,
                persona.toEntity(),
                this.codigo,
                this.facultad,
                this.departamento,
                this.tipoVinculacion,
                this.escalafon,
                this.observacion,
                this.estado
        );
    }

    public static Docente fromEntity(DocenteEntity entity){
        if(entity == null) return null;
        return Docente.builder()
                .id(entity.getId())
                .personaId(entity.getPersona() != null ? entity.getPersona().getId() : null)
                .persona(Persona.fromEntity(entity.getPersona()))
                .codigo(entity.getCodigo())
                .facultad(entity.getFacultad())
                .departamento(entity.getDepartamento())
                .tipoVinculacion(entity.getTipoVinculacion())
                .escalafon(entity.getEscalafon())
                .observacion(entity.getObservacion())
                .estado(entity.getEstado())
                .build();
    }
}
