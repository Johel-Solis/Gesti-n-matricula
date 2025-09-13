package unicauca.edu.co.ms_gestion_maticula.app.domain.model;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.print.Doc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.AsignaturaEntity;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.DocenteAsignaturaEntity;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.DocenteEntity;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Asignatura {
    private Long id;
    private Long codigo;
    private String nombre;
    private Boolean estado;
    private Date fechaAprobacion;
    private Integer oficioFacultad;
    private Integer areaFormacion;
    private String tipo;
    private Integer creditos;
    private String objetivo;
    private String contenido;
    private Integer horasPresencial;
    private Integer horasNoPresencial;
    private Integer horasTotal;
    private List<Docente> docentes; // ids de docentes asociados

    public AsignaturaEntity toEntity(){
        AsignaturaEntity entity = AsignaturaEntity.builder()
                .idAsignatura(this.id)
                .codigoAsignatura(this.codigo)
                .nombreAsignatura(this.nombre)
                .estadoAsignatura(this.estado)
                .fechaAprobacion(this.fechaAprobacion)
                .oficioFacultad(this.oficioFacultad)
                .areaFormacion(this.areaFormacion)
                .tipoAsignatura(this.tipo)
                .creditos(this.creditos)
                .objetivoAsignatura(this.objetivo)
                .contenidoAsignatura(this.contenido)
                .horasPresencial(this.horasPresencial)
                .horasNoPresencial(this.horasNoPresencial)
                .horasTotal(this.horasTotal)
                .build();
        
        
        return entity;
    }

    public static Asignatura fromEntity(AsignaturaEntity entity){
        return Asignatura.builder()
                .id(entity.getIdAsignatura())
                .codigo(entity.getCodigoAsignatura())
                .nombre(entity.getNombreAsignatura())
                .estado(entity.getEstadoAsignatura())
                .fechaAprobacion(entity.getFechaAprobacion())
                .oficioFacultad(entity.getOficioFacultad())
                .areaFormacion(entity.getAreaFormacion())
                .tipo(entity.getTipoAsignatura())
                .creditos(entity.getCreditos())
                .objetivo(entity.getObjetivoAsignatura())
                .contenido(entity.getContenidoAsignatura())
                .horasPresencial(entity.getHorasPresencial())
                .horasNoPresencial(entity.getHorasNoPresencial())
                .horasTotal(entity.getHorasTotal())
                
                .build();
    }
}
