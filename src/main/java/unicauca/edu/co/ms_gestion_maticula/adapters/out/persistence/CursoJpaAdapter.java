package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.AsignaturaEntity;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.CursoEntity;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.DocenteEntity;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.MaterialApoyoEntity;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository.AsignaturaJpaRepository;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository.CursoJpaRepository;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository.DocenteJpaRepository;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository.PeriodoJpaRepository;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository.MaterialApoyoJpaRepository;

import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Curso;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Docente;
import unicauca.edu.co.ms_gestion_maticula.app.ports.out.CursoRepository;

@Component
@RequiredArgsConstructor
public class CursoJpaAdapter implements CursoRepository {

    private final CursoJpaRepository cursoRepo;
    private final AsignaturaJpaRepository asignaturaRepo;
    private final DocenteJpaRepository docenteRepo;
    private final PeriodoJpaRepository periodoRepo;
    private final MaterialApoyoJpaRepository materialRepo;

    @Override
    public boolean existsByGrupoAndPeriodoIdAndAsignaturaId(String grupo, Long periodoId, Long asignaturaId) {
        return cursoRepo.existsByGrupoAndPeriodoAndAsignatura(grupo, periodoId, asignaturaId);
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
    public List<Curso> findAllCursos(Long idArea, Long idAsignatura, Long idPeriodo) {
        return cursoRepo.findByPeriodoIdAndIdAreaAndIdAsignatura(idPeriodo, idArea, idAsignatura).stream()
                .map(CursoEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<Curso> findCursoById(Long cursoId) {
        return cursoRepo.findById(cursoId).map(CursoEntity::toDomain);
    }

    @Override
    public Curso saveCurso(Curso curso) {
    // Cargar entidades gestionadas por JPA
    AsignaturaEntity asignatura = asignaturaRepo.findById(curso.getAsignatura().getId())
        .orElseThrow(() -> new IllegalArgumentException("Asignatura no encontrada"));

    var periodo = periodoRepo.findById(curso.getPeriodo().getId())
        .orElseThrow(() -> new IllegalArgumentException("Período no encontrado"));

    List<Long> docentesIds = curso.getDocentes() == null ? new ArrayList<>() :
        curso.getDocentes().stream().map(d -> d.getId()).collect(Collectors.toList());
    List<DocenteEntity> docentes =
        docentesIds.isEmpty() ? new ArrayList<>() :
        new ArrayList<>(docenteRepo.findAllById(docentesIds));

    List<Long> materialesIds = curso.getMateriales()==null? new ArrayList<>() :
        curso.getMateriales().stream().map(m -> m.getId()).collect(Collectors.toList());
    List<MaterialApoyoEntity> materiales =
        materialesIds.isEmpty()? new ArrayList<>() : new ArrayList<>(materialRepo.findAllById(materialesIds));

    CursoEntity entity = CursoEntity.builder()
        .id(curso.getId())
        .grupo(curso.getGrupo())
        .periodo(periodo)
        .asignatura(asignatura)
        .docentes(docentes)
    .horario(curso.getHorario())
        .salon(curso.getSalon())
        .observacion(curso.getObservacion())
    .materiales(materiales)
        .build();

    CursoEntity saved = cursoRepo.save(entity);
    return saved.toDomain();
    }

    @Override
    public void deleteCurso(Long cursoId) {
        cursoRepo.deleteById(cursoId);
    }

    @Override
    public List<Curso> findCursosByAsignaturaId(Long asignaturaId) {
        return cursoRepo.findByAsignatura(asignaturaId).stream().map(CursoEntity::toDomain).toList();
    }

    @Override
    public List<Asignatura> findAsignaturasByStatus(Boolean status) {
        return asignaturaRepo.findByEstadoAsignatura(status).stream()
                .map(AsignaturaEntity::toDomain)
                .toList();
    }

    @Override
    public List<Docente> findDocentesByAsignaturaId(Long asignaturaId) {
        return docenteRepo.findByAsignaturaId(asignaturaId).stream()
                .map(DocenteEntity::toDomain)
                .toList();
    }

    

}
