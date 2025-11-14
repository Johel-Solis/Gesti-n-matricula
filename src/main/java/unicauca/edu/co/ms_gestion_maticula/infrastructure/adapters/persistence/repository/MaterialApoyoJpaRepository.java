package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.MaterialApoyoEntity;

public interface MaterialApoyoJpaRepository extends JpaRepository<MaterialApoyoEntity, Long> {
    Optional<MaterialApoyoEntity> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}
