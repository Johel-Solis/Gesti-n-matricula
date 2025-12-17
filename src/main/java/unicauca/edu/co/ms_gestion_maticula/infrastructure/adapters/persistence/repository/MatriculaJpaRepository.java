package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import unicauca.edu.co.ms_gestion_maticula.domain.model.Matricula;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.AsignaturaEntity;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.MatriculaCursoDto;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.MatriculaEntity;

@Repository
public interface MatriculaJpaRepository extends JpaRepository<MatriculaEntity, Long> {

    List<MatriculaEntity> findByEstudianteId(Long estudianteId);



    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
           "FROM MatriculaEntity m " +
           "WHERE m.estudiante.id = :estudianteId " +
           "AND m.estado = :estado " +
           "AND m.periodo.id = :periodoId " +
           "AND m.curso.asignatura.id = :asignaturaId")
    boolean existsMatriculaByEstudianteIdAndPeriodoIdAndAsignaturaId(Long estudianteId, Long periodoId, Long asignaturaId, boolean estado);


    @Query("SELECT m FROM MatriculaEntity m WHERE m.estudiante.id = :estudianteId AND m.curso.asignatura.id = :asignaturaId AND m.estado = :estado")
    List<MatriculaEntity> findByEstudianteIdAndCursoAsignaturaId(Long estudianteId, Long asignaturaId, boolean estado);


    @Query("SELECT m.curso.asignatura FROM MatriculaEntity m WHERE m.estudiante.id = :idEstudiante AND m.periodo.id = :idPeriodo AND m.estado = :estado")
    List<AsignaturaEntity> findAsignaturasByEstudianteIdAndPeriodoId(Long idEstudiante, Long idPeriodo, boolean estado);

    @Query("""
    SELECT new MatriculaCursoDto(
        m.curso,
        COUNT(m.id)
    )
    FROM MatriculaEntity m
    WHERE m.periodo.id = :periodoId
      AND (:estado IS NULL OR :estado = '' OR m.estadoMatricula = :estado)
      AND (:estudiante IS NULL OR :estudiante = 0 OR m.estudiante.id = :estudiante)
      AND (:asignatura IS NULL OR :asignatura = 0 OR m.curso.asignatura.id = :asignatura)
    GROUP BY
       m.curso.id,
        m.curso.grupoCurso,
        m.curso.asignatura.id,
        m.curso.estado
""")
    List<MatriculaCursoDto> getListMatricula(
        @Param("periodoId") Long periodoId,
        @Param("estado") String estado,
        @Param("asignatura") Long asignatura,
        @Param("estudiante") Long estudiante);

}
