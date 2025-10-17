package unicauca.edu.co.ms_gestion_maticula.app.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.MaterialApoyo;
import unicauca.edu.co.ms_gestion_maticula.app.domain.request.MaterialApoyoRequest;
import unicauca.edu.co.ms_gestion_maticula.app.domain.response.MaterialApoyoResponse;
import unicauca.edu.co.ms_gestion_maticula.app.ports.In.MaterialApoyoService;
import unicauca.edu.co.ms_gestion_maticula.app.ports.out.MaterialApoyoRepository;

@Service
@RequiredArgsConstructor
public class MaterialApoyoServiceImpl implements MaterialApoyoService {

    @Autowired
    private final MaterialApoyoRepository repository;
    @Autowired
    private final ModelMapper mapper;

    @Autowired
    @Qualifier("messageResourceMatricula")
    private MessageSource messageSource;

    private String msg(String key, Object... args){
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }

    @Override
    @Transactional
    public MaterialApoyoResponse crear(MaterialApoyoRequest request) {
        repository.findByNombre(request.getNombre())
            .ifPresent(m -> { throw new IllegalArgumentException(msg("material.error.nombre.duplicado"));});
        MaterialApoyo material = MaterialApoyo.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .enlace(request.getEnlace())
                .build();
        return mapper.map(repository.save(material), MaterialApoyoResponse.class);
    }

    @Override
    @Transactional
    public MaterialApoyoResponse actualizar(Long id, MaterialApoyoRequest request) {
        MaterialApoyo existente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(msg("material.error.noexiste")));
        repository.findByNombre(request.getNombre())
                .filter(m -> !m.getId().equals(id))
                .ifPresent(m -> { throw new IllegalArgumentException(msg("material.error.nombre.duplicado"));});
        existente.setNombre(request.getNombre());
        existente.setDescripcion(request.getDescripcion());
        existente.setEnlace(request.getEnlace());
        return mapper.map(repository.save(existente), MaterialApoyoResponse.class);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        repository.findById(id).orElseThrow(() -> new EntityNotFoundException(msg("material.error.noexiste")));
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public MaterialApoyoResponse obtener(Long id) {
        MaterialApoyo material = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(msg("material.error.noexiste")));
        return mapper.map(material, MaterialApoyoResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaterialApoyoResponse> listar() {
        return repository.findAll().stream()
                .map(m -> mapper.map(m, MaterialApoyoResponse.class))
                .collect(Collectors.toList());
    }
}
