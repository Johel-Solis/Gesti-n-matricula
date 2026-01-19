package unicauca.edu.co.ms_gestion_maticula.domain.ports.In;

import java.util.List;

import unicauca.edu.co.ms_gestion_maticula.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Matricula;
import unicauca.edu.co.ms_gestion_maticula.domain.request.CambioEstadoMasivoRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.request.CursoMatriculaEstudiantesRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.request.ListCursosRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.request.ListEstudianteRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.request.MatriculaCursoEstudiantesRequests;
import unicauca.edu.co.ms_gestion_maticula.domain.request.MatriculaEstadoRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.request.MatriculaEstudianteCursosRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.response.EstudianteMatriculaResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MatriculaAgrupadaResonse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MatriculaBatchResultResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MatriculaCursoResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MatriculaEstudianteCursosResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MatriculaResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.TutorNotificacionResponse;


public interface MatriculaService {

    MatriculaBatchResultResponse matricularEstudiantesEnCursos(MatriculaCursoEstudiantesRequests requests);
    MatriculaEstudianteCursosResponse matriculaEstudianteCursos(MatriculaEstudianteCursosRequest request);
    MatriculaEstudianteCursosResponse matricularCursoEstudiantes(CursoMatriculaEstudiantesRequest requests);

    Boolean validarMatriculaEstudiantes(Long estudianteId, Long cursoId);

    List<Matricula> consultarMatriculaEstudiantes(ListEstudianteRequest requests);

    List<Asignatura> obtenerAsignaturasDisponiblesporEstudiante(Long estudianteId);
    String cancelarMatricula(Long matriculaId, String motivoCancelacion);
    List<EstudianteMatriculaResponse> obtenerMatriculasPorEstudiante(Long estudianteId);
    Matricula obtenerMatriculaPorId(Long matriculaId);
    List<MatriculaAgrupadaResonse> listarMatriculas(Long periodoId, String estado, Long asignatura, Long estudiante);
    List<MatriculaCursoResponse> obtenerEstudiantesMatriculadosEnCurso(Long cursoId);
    MatriculaResponse cambiarEstadoMatricula(Long id, MatriculaEstadoRequest request);
    List<TutorNotificacionResponse> notificarMatriculasAprobadas(ListEstudianteRequest request);
    List<TutorNotificacionResponse> notificarMatriculasFinalCursos(ListCursosRequest request);
    List<MatriculaResponse> cambiarEstadoMasivoMatricula(CambioEstadoMasivoRequest request);

}
