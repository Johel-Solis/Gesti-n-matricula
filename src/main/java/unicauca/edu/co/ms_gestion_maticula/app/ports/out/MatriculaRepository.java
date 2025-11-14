package unicauca.edu.co.ms_gestion_maticula.app.ports.out;

import java.util.List;
import java.util.Optional;

import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Matricula;

public interface MatriculaRepository {
    Matricula save(Matricula matricula);
    Optional<Matricula> findById(Long id);
    List<Matricula> findAll();
    void deleteById(Long id);
    Matricula update(Matricula matricula);
    List<Matricula> findByEstudianteId(Long estudianteId);
    Boolean asignaturaGanada(Long estudianteId, Long asignaturaId);
    List<Asignatura> getAsignaturasMatriculadas(Long estudianteId, Long periodoId);
    Boolean existsMatriculaByEstudianteIdAndPeriodoIdAndAsignaturaId(Long estudianteId, Long periodoId, Long asignaturaId);


}
