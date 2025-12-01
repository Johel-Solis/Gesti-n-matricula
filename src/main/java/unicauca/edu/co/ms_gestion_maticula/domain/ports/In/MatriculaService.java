package unicauca.edu.co.ms_gestion_maticula.domain.ports.In;

import java.util.List;

import unicauca.edu.co.ms_gestion_maticula.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Matricula;
import unicauca.edu.co.ms_gestion_maticula.domain.request.MatriculaCursoEstudiantesRequests;
import unicauca.edu.co.ms_gestion_maticula.domain.request.MatriculaEstudianteCursosRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MatriculaBatchResultResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MatriculaResponse;

public interface MatriculaService {

    MatriculaBatchResultResponse matricularEstudiantesEnCursos(MatriculaCursoEstudiantesRequests requests);
    List<MatriculaResponse> matriculaEstudianteCursos(MatriculaEstudianteCursosRequest request);
    MatriculaBatchResultResponse matricularCursoEstudiantes(MatriculaCursoEstudiantesRequests requests);

    Boolean validarMatriculaEstudiantes(Long estudianteId, Long cursoId);
    List<Matricula> consultarMatriculaEstudiantes(MatriculaCursoEstudiantesRequests requests);
    List<Asignatura> obtenerAsignaturasDisponiblesporEstudiante(Long estudianteId);

}
