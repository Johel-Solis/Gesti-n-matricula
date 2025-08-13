package unicauca.edu.co.ms_gestion_maticula.app.domain.request;

import java.time.LocalDate;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.app.domain.enums.PeriodoEstadoEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoAcademicoRequest {

    @NotNull(message = "{periodo.fechaInicio.notnull}")
    private LocalDate fechaInicio;

    @NotNull(message = "{periodo.fechaFin.notnull}")
    private LocalDate fechaFin;

    @NotNull(message = "{periodo.fechaFinMatricula.notnull}")
    private LocalDate fechaFinMatricula;

    @NotNull(message = "{periodo.tagPeriodo.notnull}")
    @Min(value = 1, message = "{periodo.tagPeriodo.range}")
    @Max(value = 2, message = "{periodo.tagPeriodo.range}")
    private Integer tagPeriodo;

    @Size(max = 255, message = "{periodo.descripcion.size}")
    private String descripcion;

    private PeriodoEstadoEnum estado; 



    @AssertTrue(message = "{periodo.rangoFechas.invalido}")
    public boolean isRangoFechasValido() {
        if (fechaInicio == null || fechaFin == null) return true; 
        return !fechaFin.isBefore(fechaInicio);
    }

    @AssertTrue(message = "{periodo.fechaMatricula.fueraRango}")
    public boolean isFechaMatriculaDentroDelPeriodo() {
        if (fechaInicio == null || fechaFin == null || fechaFinMatricula == null) return true;
        return !fechaFinMatricula.isBefore(fechaInicio) && !fechaFinMatricula.isAfter(fechaFin);
    }

}
