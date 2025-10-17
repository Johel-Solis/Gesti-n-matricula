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

}
