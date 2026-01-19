package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.CursoEntity;
@Repository
public interface CursoJpaRepository extends JpaRepository<CursoEntity, Long> {

    @Query("SELECT COUNT(c) > 0 FROM CursoEntity c WHERE c.asignatura.idAsignatura = :asignaturaId AND c.grupo = :grupo AND c.periodo.id = :periodoId")
    public boolean existsByGrupoAndPeriodoAndAsignatura(String grupo, Long periodoId, Long asignaturaId);

    @Query("SELECT c FROM CursoEntity c WHERE c.asignatura.idAsignatura = :asignaturaId ")
    public List<CursoEntity> findByAsignatura(Long asignaturaId);

    @Query("SELECT c FROM CursoEntity c   WHERE (:idAsignatura IS NULL OR c.asignatura.id = :idAsignatura) AND (:periodoId IS NULL OR c.periodo.id = :periodoId) AND (:idArea IS NULL OR c.asignatura.areaFormacion = :idArea)")
    public List<CursoEntity> findByPeriodoIdAndIdAreaAndIdAsignatura(@Param("periodoId") Long periodoId,
        @Param("idArea") Long idArea,
        @Param("idAsignatura") Long idAsignatura);

    @Query("SELECT c FROM CursoEntity c WHERE c.asignatura.id IN :asignaturaIds AND c.periodo.id = :periodoId")
    public List<CursoEntity> findByAsignaturas(List<Long> asignaturaIds, Long periodoId);

    @Query("SELECT DISTINCT m.curso FROM MatriculaEntity m WHERE m.periodo.id = :periodoId AND m.estadoMatricula = :estadoMatricula")
    public List<CursoEntity> getCursosByPeriodoIdAndEstadoMatricula(Long periodoId, String estadoMatricula);
}
