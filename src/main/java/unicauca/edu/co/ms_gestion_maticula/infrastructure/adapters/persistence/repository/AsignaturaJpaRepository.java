package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.AreaFormacionEntity;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.AsignaturaEntity;

public interface AsignaturaJpaRepository extends JpaRepository<AsignaturaEntity, Long> {
    Optional<AsignaturaEntity> findByCodigoAsignatura(Long codigoAsignatura);

    @Query("SELECT a FROM AsignaturaEntity a WHERE (:idArea IS NULL OR a.areaFormacion = :idArea)")
    List<AsignaturaEntity> findByEstadoAsignaturaAndAreaFormacion(Boolean estadoAsignatura, Long idArea);

    @Query("SELECT af FROM AsignaturaEntity a JOIN AreaFormacionEntity af ON af.idArea = a.areaFormacion WHERE a.estadoAsignatura = true")
    List<AreaFormacionEntity> findAllAreasFormacion();
}
