package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.DocenteEntity;

public interface DocenteJpaRepository extends JpaRepository<DocenteEntity, Long> {
    Optional<DocenteEntity> findByCodigo(String codigo);
    List<DocenteEntity> findByFacultad(String facultad);
}
