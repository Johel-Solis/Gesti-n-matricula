package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.MaterialApoyoEntity;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository.MaterialApoyoJpaRepository;
import unicauca.edu.co.ms_gestion_maticula.domain.model.MaterialApoyo;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.ports.out.MaterialApoyoRepository;

@Component
@RequiredArgsConstructor
public class MaterialApoyoJpaAdapter implements MaterialApoyoRepository {

    private final MaterialApoyoJpaRepository jpa;

    @Override
    public MaterialApoyo save(MaterialApoyo material) {
        MaterialApoyoEntity saved = jpa.save(material.toEntity());
        return saved.toDomain();
    }

    @Override
    public Optional<MaterialApoyo> findById(Long id) {
        return jpa.findById(id).map(MaterialApoyoEntity::toDomain);
    }

    @Override
    public Optional<MaterialApoyo> findByNombre(String nombre) {
        return jpa.findByNombreIgnoreCase(nombre).map(MaterialApoyoEntity::toDomain);
    }

    @Override
    public List<MaterialApoyo> findAll() {
        return jpa.findAll().stream().map(MaterialApoyoEntity::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    @Override
    public List<MaterialApoyo> findAllByIds(List<Long> ids) {
        if(ids==null || ids.isEmpty()) return List.of();
        return jpa.findAllById(ids).stream().map(MaterialApoyoEntity::toDomain).collect(Collectors.toList());
    }
}
