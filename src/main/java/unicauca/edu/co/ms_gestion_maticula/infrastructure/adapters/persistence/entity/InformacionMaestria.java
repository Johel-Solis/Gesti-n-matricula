package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity;


import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.enums.EstadoEstudianteMaestria;

@Data
@NoArgsConstructor @AllArgsConstructor @Builder // revisar cambios
@Embeddable
public class InformacionMaestria {

    @Enumerated(EnumType.STRING)
	private EstadoEstudianteMaestria estadoMaestria;
	
	private String modalidad;
	
	private String tituloDoctorado;
	private Integer Cohorte;
	private String periodoIngreso;
	
	private String modalidadIngreso;
	
	private Integer semestreAcademico;

	private Integer semestreFinanciero;
}
