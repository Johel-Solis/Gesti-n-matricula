package unicauca.edu.co.ms_gestion_maticula.app.domain.request;

import java.time.LocalDate;



import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class PeriodoFechasRequest {

    @NotNull(message = "{periodo.fechaInicio.notnull}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) 
    private LocalDate fechaInicio;

    @NotNull(message = "{periodo.fechaFin.notnull}")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaFin;

    @AssertTrue(message = "{periodo.rangoFechas.invalido}")
    public boolean isRangoValido() {
        System.out.println("Validando rango de fechas...");
        if (fechaInicio == null || fechaFin == null) return true;
        return !fechaFin.isBefore(fechaInicio);
    }

}
