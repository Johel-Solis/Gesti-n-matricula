package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import unicauca.edu.co.ms_gestion_maticula.domain.enums.EstadoEstudianteMaestria;

import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.EstudianteEntity;

public interface EstudianteJpaRepository extends JpaRepository<EstudianteEntity, Long> {

    @Query("SELECT e FROM EstudianteEntity e WHERE e.id = :id AND e.informacionMaestria.estadoMaestria = :estado")
    Optional<EstudianteEntity> getEstudianteByIdAndEstado(Long id, EstadoEstudianteMaestria estado);

    @Query("""
            SELECT e
            FROM EstudianteEntity e
            WHERE e.informacionMaestria.estadoMaestria = 'ACTIVO'
            AND NOT EXISTS (
                SELECT 1
                FROM MatriculaCalificacion mc
                WHERE mc.matricula.estudiante.id = e.id
                  AND mc.asignatura.id = :idAsignatura
                  AND mc.esDefinitiva = true
                  AND mc.nota >= :notaMinima
            )
            AND NOT EXISTS (
                SELECT 1
                FROM MatriculaEntity m
                WHERE m.estudiante.id = e.id
                  AND m.curso.asignatura.id = :idAsignatura
                  AND m.periodo.id = :idPeriodo
            )
            AND (
                SELECT COUNT(mc2)
                FROM MatriculaCalificacion mc2
                WHERE mc2.matricula.estudiante.id = e.id
                  AND mc2.asignatura.id = :idAsignatura
                  AND mc2.esDefinitiva = true
                  AND mc2.nota < :notaMinima
            ) < 2
            """)
    List<EstudianteEntity> findEstudiantesDisponiblesPorAsignatura(
            @Param("idAsignatura") Long idAsignatura,
            @Param("idPeriodo") Long idPeriodo,
            @Param("notaMinima") double notaMinima);


    @Query("SELECT e FROM EstudianteEntity e WHERE e.informacionMaestria.estadoMaestria = 'ACTIVO'")
    List<EstudianteEntity> getEstudiantesActivos();


    @Query("SELECT de.estudiante FROM DocenteEstudiante de WHERE de.docente.id = :tutorId")
    List<EstudianteEntity> findByTutor(Long tutorId);

    @Query("SELECT DISTINCT m.estudiante  FROM MatriculaEntity m WHERE m.estado = true AND m.estadoMatricula = :estadoMatricula AND m.periodo.id = :periodoId")
    List<EstudianteEntity> findByPeriodo( @Param("periodoId") Long periodoId,@Param("estadoMatricula") String estadoMatricula);

    @Query("SELECT CASE WHEN COUNT(de) > 0 THEN true ELSE false END FROM DocenteEstudiante de WHERE de.docente.id = :tutorId AND de.estudiante.codigo = :codigoEstudiante")
    boolean isTutorDeEstudiante(@Param("tutorId") Long tutorId, @Param("codigoEstudiante") String codigoEstudiante);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EstudianteEntity e WHERE e.id = :id AND e.informacionMaestria.estadoMaestria = :estado")
    boolean existsEstudianteActivoById(Long id, EstadoEstudianteMaestria estado);
}
