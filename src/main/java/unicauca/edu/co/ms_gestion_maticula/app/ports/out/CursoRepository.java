package unicauca.edu.co.ms_gestion_maticula.app.ports.out;

import java.util.List;
import java.util.Optional;

import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Curso;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Docente;

public interface CursoRepository {

    boolean existsByGrupoAndPeriodoIdAndAsignaturaId(String grupo, Long periodoId, Long asignaturaId);
    Optional<Asignatura> findAsignaturaById(Long asignaturaId);
    List<Docente> findDocentesByIds(List<Long> docenteIds);
    List<Curso> findAllCursos();
    Optional<Curso> findCursoById(Long cursoId);
    Curso saveCurso(Curso curso);
    void deleteCurso(Long cursoId);
    List<Curso> findCursosByAsignaturaId(Long asignaturaId);

}
