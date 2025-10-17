package unicauca.edu.co.ms_gestion_maticula.app.ports.In;

import java.util.List;

import unicauca.edu.co.ms_gestion_maticula.app.domain.request.CursoRequest;
import unicauca.edu.co.ms_gestion_maticula.app.domain.response.AsignaturaResponse;
import unicauca.edu.co.ms_gestion_maticula.app.domain.response.CursoResponse;
import unicauca.edu.co.ms_gestion_maticula.app.domain.response.DocenteResponse;

public interface CusoService {

    public CursoResponse crearCurso (CursoRequest request);
    public CursoResponse obtenerCursoPorId(Long id);
    public void eliminarCurso(Long id);
    public CursoResponse actualizarCurso(Long id, CursoRequest request);
    public boolean existeCurso(String grupo, Long asignaturaId);
    public boolean existeCursoPorId(Long id);
    public List<CursoResponse> obtenerTodosLosCursos();
    public List<AsignaturaResponse> obtenerAsignaturasPorEstado();
    public List<DocenteResponse> obtenerDocentesPorAsignaturaId(Long asignaturaId);

}
