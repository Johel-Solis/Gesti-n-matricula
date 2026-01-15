package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Estudiante;


@Data   @AllArgsConstructor @NoArgsConstructor
@Entity @Table(name = "estudiantes")
@Builder
public class EstudianteEntity {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_persona")
	private PersonaEntity persona;
		
	
    @Column(unique = true)
	private String codigo;
	private String ciudadResidencia;
	
	@Column(unique = true)
	private String correoUniversidad;
	private LocalDate fechaGrado;
	private String tituloPregrado;
	private String observacion;
	
	@Embedded
	private InformacionMaestria informacionMaestria;
	

    public Estudiante toDomain(){
        return Estudiante.builder()
                .id(this.id)
                .persona(this.persona.toDomain())
                .codigo(this.codigo)
                .ciudadResidencia(this.ciudadResidencia)
                .correoUniversidad(this.correoUniversidad)
                .fechaGrado(this.fechaGrado)
                .tituloPregrado(this.tituloPregrado)
                .observacion(this.observacion)
                .informacionMaestria(this.informacionMaestria)
                .build();
    }
	
	

}
