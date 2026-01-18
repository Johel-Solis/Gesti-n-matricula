package unicauca.edu.co.ms_gestion_maticula.domain.ports.out;

import java.util.List;
import java.util.Optional;

import unicauca.edu.co.ms_gestion_maticula.domain.enums.EstadoEstudianteMaestria;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Estudiante;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Matricula;
import unicauca.edu.co.ms_gestion_maticula.domain.model.MatriculaCurso;

public interface MatriculaRepository {
    Matricula save(Matricula matricula);
    Optional<Matricula> findById(Long id);
    List<Matricula> findAll();
    List<MatriculaCurso> getListMatriculas(Long periodoId, String estado, Long asignatura, Long estudiante);
    void deleteById(Long id);
    Matricula update(Matricula matricula);
    List<Matricula> findByEstudianteId(Long estudianteId);
    Boolean asignaturaGanada(Long estudianteId, Long asignaturaId);
    List<Asignatura> getAsignaturasMatriculadas(Long estudianteId, Long periodoId);
    Boolean existsMatriculaByEstudianteIdAndPeriodoIdAndAsignaturaId(Long estudianteId, Long periodoId, Long asignaturaId);
    Optional<Estudiante> getEstudianteById(Long estudianteId);
    Optional<Estudiante> getEstudianteByIdAndEstado(Long estudianteId, EstadoEstudianteMaestria estado);
    List<Matricula> findByEstudianteIdAndPeriodoActivo(Long estudianteId);
    List<Matricula> findByCursoIdAndPeriodoId(Long cursoId, Long periodoId);
    Optional<Matricula> findNotaFinalByMatriculaId(Long idMatricula);


}
