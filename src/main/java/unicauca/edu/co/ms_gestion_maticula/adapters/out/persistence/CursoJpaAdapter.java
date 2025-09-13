package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.AsignaturaEntity;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.CursoEntity;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository.AsignaturaJpaRepository;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository.cursoJpaRepository;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository.DocenteJpaRepository;

import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Curso;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Docente;
import unicauca.edu.co.ms_gestion_maticula.app.ports.out.CursoRepository;

@Component
@RequiredArgsConstructor
public class CursoJpaAdapter implements CursoRepository {

    private final cursoJpaRepository cursoRepo;
    private final AsignaturaJpaRepository asignaturaRepo;
    private final DocenteJpaRepository docenteRepo;

    @Override
    public boolean existsByGrupoAndPeriodoIdAndAsignaturaId(String grupo, Long periodoId, Long asignaturaId) {
        return cursoRepo.existsByGrupoAndPeriodo_IdAndAsignatura_Id(grupo, periodoId, asignaturaId);
    }

    @Override
    public Optional<Asignatura> findAsignaturaById(Long asignaturaId) {
        return asignaturaRepo.findById(asignaturaId)
                .map(Asignatura::fromEntity);
    }

    @Override
    public List<Docente> findDocentesByIds(List<Long> docenteIds) {
        return docenteRepo.findAllById(docenteIds).stream()
                .map(Docente::fromEntity)
                .toList();
    }

    @Override
    public List<Curso> findAllCursos() {
        return cursoRepo.findAll().stream()
                .map(CursoEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Curso> findCursoById(Long cursoId) {
        return cursoRepo.findById(cursoId).map(CursoEntity::toDomain);
    }

    @Override
    public Curso saveCurso(Curso curso) {
        CursoEntity entity = curso.toEntity();
        CursoEntity saved = cursoRepo.save(entity);
        return saved.toDomain();
    }

    @Override
    public void deleteCurso(Long cursoId) {
        cursoRepo.deleteById(cursoId);
    }

    @Override
    public List<Curso> findCursosByAsignaturaId(Long asignaturaId) {
        return cursoRepo.findByAsignatura_Id(asignaturaId).stream().map(CursoEntity::toDomain).toList();
    }

}
