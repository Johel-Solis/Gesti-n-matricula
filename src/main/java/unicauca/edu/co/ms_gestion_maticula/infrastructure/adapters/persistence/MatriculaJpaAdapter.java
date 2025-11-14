package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.MatriculaEntity;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository.MatriculaCalificacionRepository;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository.MatriculaJpaRepository;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Matricula;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.ports.out.MatriculaRepository;

@Component
@RequiredArgsConstructor
public class MatriculaJpaAdapter implements MatriculaRepository {

    private final MatriculaJpaRepository repository;
    private final MatriculaCalificacionRepository matriculaCalificacionRepository;

    @Override
    public Matricula save(Matricula matricula) {
        MatriculaEntity saved = repository.save(matricula.toEntity());
        return new Matricula().fromEntity(saved);
    }

    @Override
    public Optional<Matricula> findById(Long id) {
        return repository.findById(id).map(entity -> new Matricula().fromEntity(entity));
    }

    @Override
    public List<Matricula> findAll() {
        return repository.findAll().stream()
                .map(entity -> new Matricula().fromEntity(entity))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);            
    }

    @Override
    public Matricula update(Matricula matricula) {
        MatriculaEntity updated = repository.save(matricula.toEntity());
        return new Matricula().fromEntity(updated);
    }

    @Override
    public List<Matricula> findByEstudianteId(Long estudianteId) {
        
        return repository.findByEstudianteId(estudianteId).stream()
                .map(entity -> new Matricula().fromEntity(entity))
                .toList();
    }

    @Override
    public Boolean asignaturaGanada(Long estudianteId, Long asignaturaId) {
        return matriculaCalificacionRepository.asignaturaGanada(estudianteId, asignaturaId);
    }

    @Override
    public List<Asignatura> getAsignaturasMatriculadas(Long estudianteId, Long periodoId) {
        return repository.findAsignaturasByEstudianteIdAndPeriodoId(estudianteId, periodoId,true).stream()
                .map(Asignatura::fromEntity)
                .toList();
    }

    @Override
    public Boolean existsMatriculaByEstudianteIdAndPeriodoIdAndAsignaturaId(Long estudianteId, Long periodoId,
            Long asignaturaId) {
        return repository.existsMatriculaByEstudianteIdAndPeriodoIdAndAsignaturaId(estudianteId, periodoId, asignaturaId,true);
    }
    
}
