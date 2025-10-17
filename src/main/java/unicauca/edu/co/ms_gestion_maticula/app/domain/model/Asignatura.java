package unicauca.edu.co.ms_gestion_maticula.app.domain.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.AsignaturaEntity;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asignatura {
    private Long id;
    private Long codigo;
    private String nombre;
    private Boolean estado;
    private Integer areaFormacion;
    private String tipo;
    private Integer creditos;
   
    private List<Docente> docentes; // ids de docentes asociados

    public AsignaturaEntity toEntity(){
        AsignaturaEntity entity = AsignaturaEntity.builder()
                .idAsignatura(this.id)
                .codigoAsignatura(this.codigo)
                .nombreAsignatura(this.nombre)
                .estadoAsignatura(this.estado)
                .areaFormacion(this.areaFormacion)
                .tipoAsignatura(this.tipo)
                .creditos(this.creditos)
                .build();
                
        
        
        return entity;
    }

    public static Asignatura fromEntity(AsignaturaEntity entity){
        return Asignatura.builder()
                .id(entity.getIdAsignatura())
                .codigo(entity.getCodigoAsignatura())
                .nombre(entity.getNombreAsignatura())
                .estado(entity.getEstadoAsignatura())
                .areaFormacion(entity.getAreaFormacion())
                .tipo(entity.getTipoAsignatura())
                .creditos(entity.getCreditos())                
                .build();
    }
}
