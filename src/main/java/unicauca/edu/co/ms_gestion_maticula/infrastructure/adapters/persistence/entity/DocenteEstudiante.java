package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Entity 
@Table(name = "docente_estudiante",uniqueConstraints = {
		@UniqueConstraint(columnNames = {"id_docente","id_estudiante"})
		})
public class DocenteEstudiante {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "id_docente")
	private DocenteEntity docente;
	
	@ManyToOne
	@JoinColumn(name = "id_estudiante")
	private EstudianteEntity estudiante;
	
	private String tipo;	

}
