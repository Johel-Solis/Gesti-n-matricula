package unicauca.edu.co.ms_gestion_maticula.domain.ports.ports.In;

import java.util.List;

import unicauca.edu.co.ms_gestion_maticula.domain.model.AreaFormacion;
import unicauca.edu.co.ms_gestion_maticula.domain.request.CursoRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.response.AsignaturaResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.CursoResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.DocenteResponse;

public interface CusoService {

    public CursoResponse crearCurso (CursoRequest request);
    public CursoResponse obtenerCursoPorId(Long id);
    public void eliminarCurso(Long id);
    public CursoResponse actualizarCurso(Long id, CursoRequest request);
    public boolean existeCurso(String grupo, Long asignaturaId);
    public boolean existeCursoPorId(Long id);
    public List<CursoResponse> obtenerTodosLosCursos(Long idArea, Long idAsignatura, Long idPeriodo);
    public List<AsignaturaResponse> obtenerAsignaturasPorEstado(Long idArea);
    public List<DocenteResponse> obtenerDocentesPorAsignaturaId(Long asignaturaId);
    public List<AreaFormacion> obtenerAreasFormacion();
    

}
