package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity;



import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Docente;

@Data   @AllArgsConstructor
@Entity @Table(name = "docentes")
public class DocenteEntity {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_persona")
	private	PersonaEntity persona;
	
	@Column(unique = true)
	private String codigo;
	
	private String facultad;
	
	private String departamento;

	private String tipoVinculacion;

	private String escalafon;
	
	private String observacion;
		
	private String estado;

	public Docente toDomain() {
		return Docente.builder()
				.id(this.id)
				.persona(this.persona != null ? this.persona.toDomain() : null)
				.codigo(this.codigo)
				.facultad(this.facultad)
				.departamento(this.departamento)
				.tipoVinculacion(this.tipoVinculacion)
				.escalafon(this.escalafon)
				.observacion(this.observacion)
				.estado(this.estado)
				.build();
	}

}
