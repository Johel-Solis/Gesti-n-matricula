package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.CursoEntity;

public interface cursoJpaRepository extends JpaRepository<CursoEntity, Long> {
    
    boolean existsByGrupoAndPeriodo_IdAndAsignatura_Id(String grupo, Long periodoId, Long asignaturaId);
    java.util.List<CursoEntity> findByAsignatura_Id(Long asignaturaId);

}
