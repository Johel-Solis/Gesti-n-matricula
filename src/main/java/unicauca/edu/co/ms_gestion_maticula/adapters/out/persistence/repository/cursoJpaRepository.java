package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.CursoEntity;
@Repository
public interface CursoJpaRepository extends JpaRepository<CursoEntity, Long> {

    @Query("SELECT COUNT(c) > 0 FROM CursoEntity c WHERE c.asignatura.idAsignatura = :asignaturaId AND c.grupo = :grupo AND c.periodo.id = :periodoId")
    public boolean existsByGrupoAndPeriodoAndAsignatura(String grupo, Long periodoId, Long asignaturaId);

    @Query("SELECT c FROM CursoEntity c WHERE c.asignatura.idAsignatura = :asignaturaId ")
    public List<CursoEntity> findByAsignatura(Long asignaturaId);

    @Query("SELECT c FROM CursoEntity c JOIN AsignaturaEntity a ON c.asignatura.id = a.id WHERE (:periodoId IS NULL OR c.periodo.id = :periodoId) AND (:idArea IS NULL OR a.areaFormacion = :idArea) AND (:idAsignatura IS NULL OR a.id = :idAsignatura)")
    public List<CursoEntity> findByPeriodoIdAndIdAreaAndIdAsignatura(Long periodoId, Long idArea, Long idAsignatura);

}
