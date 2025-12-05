package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.EstudianteEntity;

public interface EstrudianteJpaRepository extends JpaRepository<EstudianteEntity, Long> {


    @Query("SELECT e FROM EstudianteEntity e WHERE e.id = :id AND e.estadoMatricula = :estado")
    Optional<EstudianteEntity> findByIdAndEstado(Long id, String estado);
}
