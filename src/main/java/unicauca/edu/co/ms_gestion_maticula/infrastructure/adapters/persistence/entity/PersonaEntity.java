package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Persona;

@Data   @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "personas")
public class PersonaEntity {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true)
	private Long identificacion;
	
	private String nombre;
    
	private String apellido;
	
	@Column(unique = true)
	private String correoElectronico;

	private String telefono;
	
	private String genero;

	private String tipoIdentificacion;

	public Persona toDomain() {
		return Persona.builder()
				.id(this.id)
				.identificacion(this.identificacion)
				.nombre(this.nombre)
				.apellido(this.apellido)
				.correoElectronico(this.correoElectronico)
				.telefono(this.telefono)
				.genero(this.genero)
				.tipoIdentificacion(this.tipoIdentificacion)
				.build();
	}
}
