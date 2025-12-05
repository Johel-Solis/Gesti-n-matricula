package unicauca.edu.co.ms_gestion_maticula.domain.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.EstudianteEntity;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.InformacionMaestria;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Estudiante {
    
	private Long id;
	private Persona persona;
	private String codigo;
	private String ciudadResidencia;
	private String correoUniversidad;
	private LocalDate fechaGrado;
	private String tituloPregrado;
	private String observacion;
	private InformacionMaestria informacionMaestria;


    public EstudianteEntity toEntity(){
        return EstudianteEntity.builder()
                .id(this.id)
                .persona(this.persona.toEntity())
                .codigo(this.codigo)
                .ciudadResidencia(this.ciudadResidencia)
                .correoUniversidad(this.correoUniversidad)
                .fechaGrado(this.fechaGrado)
                .tituloPregrado(this.tituloPregrado)
                .observacion(this.observacion)
                .informacionMaestria(this.informacionMaestria)
                .build();
    }
    
    public static Estudiante fromEntity(EstudianteEntity entity) {
        return Estudiante.builder()
                .id(entity.getId())
                .persona(Persona.fromEntity(entity.getPersona()))
                .codigo(entity.getCodigo())
                .ciudadResidencia(entity.getCiudadResidencia())
                .correoUniversidad(entity.getCorreoUniversidad())
                .fechaGrado(entity.getFechaGrado())
                .tituloPregrado(entity.getTituloPregrado())
                .observacion(entity.getObservacion())
                .informacionMaestria(entity.getInformacionMaestria())
                .build();
    }

}