package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.ForeignKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Curso;

@Entity
@Table(name = "cursos",
       uniqueConstraints = {
         @UniqueConstraint(name = "uk_curso_grupo_periodo_asignatura",
                           columnNames = {"grupo", "periodo_id", "asignatura_id"})
       })
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CursoEntity {
    
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grupocurso", nullable=false, length=20)
    private String grupo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "periodo_id", nullable = false,
                foreignKey = @ForeignKey(name = "cursos_periodo_academico_FK"))
    private PeriodoAcademicoEntity periodo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_asignatura", nullable = false,
                foreignKey = @ForeignKey(name = "cursos_asignaturas_FK"))
    private AsignaturaEntity asignatura;

    @ManyToMany
    @JoinTable(
        name = "curso_docente",
        joinColumns = @JoinColumn(name = "id_curso"),
        inverseJoinColumns = @JoinColumn(name = "id_docente"),
        uniqueConstraints = @UniqueConstraint(name="uk_curso_docente", columnNames = {"id_curso","id_docente"})
    )
    private Set<DocenteEntity> docentes = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "curso_material",
        joinColumns = @JoinColumn(name = "id_curso"),
        inverseJoinColumns = @JoinColumn(name = "id_material"),
        uniqueConstraints = @UniqueConstraint(name="uk_curso_material", columnNames = {"id_curso","id_material"})
    )
    @Builder.Default
    private Set<MaterialApoyoEntity> materiales = new HashSet<>();

    @Column(name = "horariocurso", nullable=false, length=100)
    private String horario;

    @Column(name = "saloncurso", nullable=false, length=50)
    private String salon;

    @Column(name = "observacioncurso", nullable=true, length=255)
    private String observacion;

    public Curso toDomain() {
        return Curso.builder()
                .id(this.id)
                .grupo(this.grupo)
                .periodo(this.periodo.toDomain())
                .asignatura(this.asignatura.toDomain())
                .docentes(this.docentes.stream()
                        .map(DocenteEntity::toDomain)
                        .collect(Collectors.toSet()))
        .materiales(this.materiales==null? new HashSet<>() : this.materiales.stream().map(MaterialApoyoEntity::toDomain).collect(Collectors.toSet()))
                .horario(this.horario)
                .salon(this.salon)
                .observacion(this.observacion)
                .build();
    }


}
