package unicauca.edu.co.ms_gestion_maticula.app.ports.In;

import java.util.List;

import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Matricula;
import unicauca.edu.co.ms_gestion_maticula.app.domain.request.MatriculaCursoEstudiantesRequests;
import unicauca.edu.co.ms_gestion_maticula.app.domain.request.MatriculaEstudianteCursosRequest;

public interface MatriculaService {

    void matricularEstudiantesEnCursos(MatriculaCursoEstudiantesRequests requests);
    void matriculaEstudianteCursos(MatriculaEstudianteCursosRequest request);
    void matricularCursoEstudiantes(MatriculaCursoEstudiantesRequests requests);

    Boolean validarMatriculaEstudiantes(Long estudianteId, Long cursoId);
    List<Matricula> consultarMatriculaEstudiantes(MatriculaCursoEstudiantesRequests requests);
    List<Asignatura> obtenerAsignaturasDisponiblesporEstudiante(Long estudianteId);

}
