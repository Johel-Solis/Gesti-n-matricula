package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.MaterialApoyoEntity;

public interface MaterialApoyoJpaRepository extends JpaRepository<MaterialApoyoEntity, Long> {
    Optional<MaterialApoyoEntity> findByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCase(String nombre);
}
