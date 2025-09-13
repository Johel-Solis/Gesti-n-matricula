package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.AsignaturaEntity;

public interface AsignaturaJpaRepository extends JpaRepository<AsignaturaEntity, Long> {
    Optional<AsignaturaEntity> findByCodigoAsignatura(Long codigoAsignatura);
    List<AsignaturaEntity> findByEstadoAsignatura(Boolean estadoAsignatura);
}
