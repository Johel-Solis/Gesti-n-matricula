package unicauca.edu.co.ms_gestion_maticula.domain.ports.out;

import java.util.List;
import java.util.Optional;

import unicauca.edu.co.ms_gestion_maticula.domain.enums.EstadoEstudianteMaestria;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Docente;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Estudiante;

public interface EstudianteDocenteRepository {

    Optional<Estudiante> getEstudianteById(Long estudianteId);
    Optional<Estudiante> getEstudianteByIdAndEstado(Long estudianteId, EstadoEstudianteMaestria estado);
    List<Docente> getDirectores();
    List<Estudiante> findEstudiantesDisponiblesPorAsignatura(Long asignaturaId, Long periodoId);
    List<Estudiante> getEstudiantesActivos();
    List<Docente> findDocentesByAsignaturaId(Long asignaturaId);
    List<Docente> findDocentesByIds(List<Long> docenteIds);
}
