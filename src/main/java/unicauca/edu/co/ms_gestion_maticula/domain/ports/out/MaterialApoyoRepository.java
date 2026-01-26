package unicauca.edu.co.ms_gestion_maticula.domain.ports.out;

import java.util.List;
import java.util.Optional;

import unicauca.edu.co.ms_gestion_maticula.domain.model.MaterialApoyo;

public interface MaterialApoyoRepository {
    MaterialApoyo save(MaterialApoyo material);
    Optional<MaterialApoyo> findById(Long id);
    Optional<MaterialApoyo> findByNombre(String nombre);
    List<MaterialApoyo> findAll();
    void deleteById(Long id);
    List<MaterialApoyo> findAllByIds(List<Long> ids);
    boolean isAsignadoById(Long id);
}
