package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.EstudianteEntity;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.MatriculaEntity;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository.EstudianteJpaRepository;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository.MatriculaCalificacionRepository;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository.MatriculaJpaRepository;
import unicauca.edu.co.ms_gestion_maticula.domain.enums.EstadoEstudianteMaestria;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Estudiante;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Matricula;
import unicauca.edu.co.ms_gestion_maticula.domain.model.MatriculaCurso;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.out.MatriculaRepository;

@Component
@RequiredArgsConstructor
public class MatriculaJpaAdapter implements MatriculaRepository {

    private final MatriculaJpaRepository repository;
    private final MatriculaCalificacionRepository matriculaCalificacionRepository;
    private final EstudianteJpaRepository estudianteRepository;

    @Value("${app.matricula.umbral-ganado}")
    private double umbralGanado;

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
    public List<MatriculaCurso> getListMatriculas(Long periodoId, String estado, Long asignatura, Long estudiante) {
        estado = (estado == null || estado.isBlank()) ? null : estado;
        estudiante = (estudiante != null && estudiante == 0) ? null : estudiante;
        asignatura = (asignatura != null && asignatura == 0) ? null : asignatura;

        return repository.getListMatricula( periodoId, estado, asignatura,  estudiante).stream()
                .map(entity -> new MatriculaCurso().fromEntity(entity))
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
    public List<Matricula> findByEstudianteIdAndPeriodoActivo(Long estudianteId) {
        return repository.getByEstudianteIdAndPeriodoActivo(estudianteId).stream()
                .map(entity -> new Matricula().fromEntity(entity))
                .toList();
    }

    @Override
    public Boolean asignaturaGanada(Long estudianteId, Long asignaturaId) {
        return matriculaCalificacionRepository.asignaturaGanada(estudianteId, asignaturaId, umbralGanado);
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

    @Override
    public Optional<Estudiante> getEstudianteById(Long estudianteId) {
        return estudianteRepository.findById(estudianteId)
                .map(EstudianteEntity::toDomain);
    }

    @Override
    public Optional<Estudiante> getEstudianteByIdAndEstado(Long estudianteId, EstadoEstudianteMaestria estado) {
        return estudianteRepository.getEstudianteByIdAndEstado(estudianteId, estado)
                .map(EstudianteEntity::toDomain);
    }

    @Override
    public List<Matricula> findByCursoIdAndPeriodoId(Long cursoId, Long periodoId) {
        return repository.findByCursoIdAndPeriodoId(cursoId, periodoId).stream()
                .map(entity -> new Matricula().fromEntity(entity))
                .toList();
    }
    
    public Optional<Matricula> findNotaFinalByMatriculaId(Long idMatricula) {
        return matriculaCalificacionRepository.findNotaFinalByMatriculaIdEntity(idMatricula)
                .map(entity -> new Matricula().fromEntity(entity));
    }
}
