package unicauca.edu.co.ms_gestion_maticula.app.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.CursoEntity;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Curso;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Docente;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.PeriodoAcademico;
import unicauca.edu.co.ms_gestion_maticula.app.domain.request.CursoRequest;
import unicauca.edu.co.ms_gestion_maticula.app.domain.response.CursoResponse;
import unicauca.edu.co.ms_gestion_maticula.app.ports.In.CusoService;
import unicauca.edu.co.ms_gestion_maticula.app.ports.out.CursoRepository;
import unicauca.edu.co.ms_gestion_maticula.app.ports.out.PeriodoAcademicoRepository;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl implements CusoService {

    @Autowired
    private final CursoRepository cursoRepository;

    @Autowired
    private final PeriodoAcademicoRepository periodoAcademicoRepository;
    @Autowired
    private final ModelMapper modelMapper;

   @Qualifier("messageResourceMatricula")
	MessageSource messageSource; 

    @Override
    public CursoResponse crearCurso(CursoRequest request) {
         validarUnicidad(request);

        PeriodoAcademico periodo = periodoAcademicoRepository.findById(request.getPeriodoId())
                .orElseThrow(() -> new EntityNotFoundException("Período no encontrado"));

        Asignatura asignatura = cursoRepository.findAsignaturaById(request.getAsignaturaId())
                .orElseThrow(() -> new EntityNotFoundException("Asignatura no encontrada"));

        Set<Docente> docentes = cursoRepository.findDocentesByIds(request.getDocentesIds()).stream()
                .collect(Collectors.toSet());

        Curso curso = Curso.builder()
                .grupo(request.getGrupo())
                .periodo(periodo)
                .asignatura(asignatura)
                .docentes(docentes)
                .horario(request.getHorario())
                .salon(request.getSalon())
                .observacion(request.getObservacion())
                .build();

         Curso result = cursoRepository.saveCurso(curso);
        return modelMapper.map(result, CursoResponse.class);
    }

    @Override
    public CursoResponse obtenerCursoPorId(Long id) {
        Curso curso = cursoRepository.findCursoById(id);
        return modelMapper.map(curso, CursoResponse.class);
    }

    @Override
    public void eliminarCurso(Long id) {
        if (!cursoRepository.existsByGrupoAndPeriodoIdAndAsignaturaId("", id, id)) {
            throw new EntityNotFoundException("Curso no encontrado");
        }
        cursoRepository.deleteCurso(id);
    }

    @Override
    public CursoResponse actualizarCurso(Long id, CursoRequest request) {
        // Validar unicidad excepto si es el mismo curso
        if (cursoRepository.existsByGrupoAndPeriodoIdAndAsignaturaId(request.getGrupo(), request.getPeriodoId(), request.getAsignaturaId())) {
            Curso existente = cursoRepository.findAllCursos().stream()
                .filter(c -> c.getGrupo().equals(request.getGrupo()) &&
                             c.getPeriodoId().equals(request.getPeriodoId()) &&
                             c.getAsignaturaId().equals(request.getAsignaturaId()))
                .findFirst().orElse(null);
            if (existente != null && !existente.getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe un curso con el mismo grupo, período y asignatura.");
            }
        }
        Curso curso = Curso.builder()
                .id(id)
                .grupo(request.getGrupo())
                .periodoId(request.getPeriodoId())
                .asignaturaId(request.getAsignaturaId())
                .docentesIds(request.getDocentesIds() == null ? Set.of() : Set.copyOf(request.getDocentesIds()))
                .horario(request.getHorario())
                .salon(request.getSalon())
                .observacion(request.getObservacion())
                .build();
        Curso actualizado = cursoRepository.saveCurso(curso);
        return modelMapper.map(actualizado, CursoResponse.class);
    }

    @Override
    public boolean existeCurso(String grupo, Long periodoId, Long asignaturaId) {
        return cursoRepository.existsByGrupoAndPeriodoIdAndAsignaturaId(grupo, periodoId, asignaturaId);
    }

    @Override
    public boolean existeCursoPorId(Long id) {
        try {
            cursoRepository.findCursoById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<CursoResponse> obtenerTodosLosCursos() {
        List<Curso> cursos = cursoRepository.findAllCursos();
        return cursos.stream().map(c -> modelMapper.map(c, CursoResponse.class)).toList();
    }

    // Método de búsqueda con filtros opcionales y paginación
    public List<CursoResponse> buscarCursos(String grupo, Long periodoId, Long asignaturaId, Integer page, Integer size) {
        List<Curso> cursos = cursoRepository.findAllCursos();
        // Filtrar por grupo, periodo, asignatura si se proveen
        if (grupo != null && !grupo.isBlank()) {
            cursos = cursos.stream().filter(c -> grupo.equalsIgnoreCase(c.getGrupo())).toList();
        }
        if (periodoId != null) {
            cursos = cursos.stream().filter(c -> periodoId.equals(c.getPeriodoId())).toList();
        }
        if (asignaturaId != null) {
            cursos = cursos.stream().filter(c -> asignaturaId.equals(c.getAsignaturaId())).toList();
        }
        // Paginación
        if (page != null && size != null && size > 0) {
            int from = page * size;
            int to = Math.min(from + size, cursos.size());
            if (from < cursos.size()) {
                cursos = cursos.subList(from, to);
            } else {
                cursos = List.of();
            }
        }
        return cursos.stream().map(c -> modelMapper.map(c, CursoResponse.class)).toList();
    }

    private void validarUnicidad(CursoRequest req) {
        if (cursoRepository.existsByGrupoAndPeriodoIdAndAsignaturaId(req.getGrupo(), req.getPeriodoId(), req.getAsignaturaId())) {
            throw new IllegalArgumentException("Ya existe un curso con el mismo grupo, período y asignatura.");
        }
    }
   

}
