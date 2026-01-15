package unicauca.edu.co.ms_gestion_maticula.domain.ports.out;

import java.util.List;
import java.util.Optional;

import unicauca.edu.co.ms_gestion_maticula.domain.model.AreaFormacion;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Curso;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Docente;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Estudiante;

public interface CursoRepository {

    boolean existsByGrupoAndPeriodoIdAndAsignaturaId(String grupo, Long periodoId, Long asignaturaId);
    Optional<Asignatura> findAsignaturaById(Long asignaturaId);
    List<Docente> findDocentesByIds(List<Long> docenteIds);
    List<Curso> findAllCursos(Long idArea, Long idAsignatura, Long idPeriodo);
    Optional<Curso> findCursoById(Long cursoId);
    Curso saveCurso(Curso curso);
    void deleteCurso(Long cursoId);
    List<Curso> findCursosByAsignaturaId(Long asignaturaId);
    List<AreaFormacion> findAllAreasFormacion();
    List<Asignatura> findAsignaturasByStatus(Boolean status, Long idArea);
    List<Docente> findDocentesByAsignaturaId(Long asignaturaId);
    List<Curso> getCursosByAsignaturaIds(List<Long> asignaturaIds, Long periodoId);

    List<Estudiante> findEstudiantesDisponiblesPorAsignatura(Long asignaturaId, Long periodoId);

}
