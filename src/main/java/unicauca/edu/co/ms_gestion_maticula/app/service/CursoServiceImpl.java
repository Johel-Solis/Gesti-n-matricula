package unicauca.edu.co.ms_gestion_maticula.app.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Curso;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.Docente;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.PeriodoAcademico;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.MaterialApoyo;
import unicauca.edu.co.ms_gestion_maticula.app.domain.request.CursoRequest;
import unicauca.edu.co.ms_gestion_maticula.app.domain.response.CursoResponse;
import unicauca.edu.co.ms_gestion_maticula.app.domain.response.MaterialApoyoResponse;
import unicauca.edu.co.ms_gestion_maticula.app.ports.In.CusoService;
import unicauca.edu.co.ms_gestion_maticula.app.ports.out.CursoRepository;
import unicauca.edu.co.ms_gestion_maticula.app.ports.out.PeriodoAcademicoRepository;
import unicauca.edu.co.ms_gestion_maticula.app.ports.out.MaterialApoyoRepository;

@Service
@RequiredArgsConstructor
public class CursoServiceImpl implements CusoService {

    @Autowired
    private final CursoRepository cursoRepository;

    @Autowired
    private final PeriodoAcademicoRepository periodoAcademicoRepository;
    @Autowired
    private final ModelMapper modelMapper;
    @Autowired
    private final MaterialApoyoRepository materialApoyoRepository;

    @Autowired
    @Qualifier("messageResourceMatricula")
	private MessageSource messageSource; 

    @Override
    @Transactional
    public CursoResponse crearCurso(CursoRequest request) {
        // 1) Obtener período ACTIVO y asociarlo (si no existe lanzar excepción)
    PeriodoAcademico periodo = periodoAcademicoRepository.findPeriodoActivo()
        .orElseThrow(() -> new EntityNotFoundException(msg("curso.error.periodo.activo.noexiste")));

        Long periodoActivoId = periodo.getId();

        // 2) Validar que la asignatura exista y esté activa
    Asignatura asignatura = cursoRepository.findAsignaturaById(request.getAsignaturaId())
        .orElseThrow(() -> new EntityNotFoundException(msg("curso.error.asignatura.noexiste")));
        if (asignatura.getEstado() == null || !asignatura.getEstado()) {
            throw new IllegalArgumentException(msg("curso.error.asignatura.inactiva"));
        }

        // 3) Validar docentes: existan y estén activos
        List<Long> idsSolicitados = request.getDocentesIds();
        List<Docente> docentesList = cursoRepository.findDocentesByIds(idsSolicitados);

        // Determinar faltantes
        var idsEncontrados = docentesList.stream().map(Docente::getId).collect(Collectors.toSet());
        List<Long> faltantes = idsSolicitados.stream().filter(id -> !idsEncontrados.contains(id)).toList();
        if (!faltantes.isEmpty()) {
            throw new IllegalArgumentException(msg("curso.error.docentes.noencontrados", faltantes));
        }

        // Determinar inactivos
        List<Long> inactivos = docentesList.stream()
                .filter(d -> d.getEstado() == null || !"ACTIVO".equalsIgnoreCase(d.getEstado()))
                .map(Docente::getId)
                .toList();
        if (!inactivos.isEmpty()) {
            throw new IllegalArgumentException(msg("curso.error.docentes.inactivos", inactivos));
        }

        // 4) Validar unicidad (grupo, asignatura, período ACTIVO)
        if (cursoRepository.existsByGrupoAndPeriodoIdAndAsignaturaId(request.getGrupo(), periodoActivoId, request.getAsignaturaId())) {
            throw new IllegalArgumentException(msg("curso.error.unicidad.grupo_asignatura_periodo"));
        }

        // 5) Construir y guardar curso
        Set<Docente> docentes = Set.copyOf(docentesList);

        // 6) Materiales de apoyo (opcionales) - validar existencia si se enviaron
        Set<MaterialApoyo> materiales = Set.of();
        if(request.getMaterialApoyoIds()!=null && !request.getMaterialApoyoIds().isEmpty()){
            List<MaterialApoyo> mats = materialApoyoRepository.findAllByIds(request.getMaterialApoyoIds());
            var encontrados = mats.stream().map(MaterialApoyo::getId).collect(Collectors.toSet());
            List<Long> faltantesMat = request.getMaterialApoyoIds().stream().filter(mid -> !encontrados.contains(mid)).toList();
            if(!faltantesMat.isEmpty()){
                throw new IllegalArgumentException(msg("curso.error.materiales.noencontrados", faltantesMat));
            }
            materiales = Set.copyOf(mats);
        }

        Curso curso = Curso.builder()
                .grupo(request.getGrupo())
                .periodo(periodo)
                .asignatura(asignatura)
                .docentes(docentes)
                .materiales(materiales)
                .horario(request.getHorario())
                .salon(request.getSalon())
                .observacion(request.getObservacion())
                .build();

    Curso result = cursoRepository.saveCurso(curso);
    CursoResponse resp = modelMapper.map(result, CursoResponse.class);
    if(result.getMateriales()!=null){
        resp.setMateriales(result.getMateriales().stream()
        .map(m -> MaterialApoyoResponse.builder()
            .id(m.getId())
            .nombre(m.getNombre())
            .descripcion(m.getDescripcion())
            .enlace(m.getEnlace())
            .build())
        .toList());
    }
    return resp;
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponse obtenerCursoPorId(Long id) {
    Curso curso = cursoRepository.findCursoById(id)
        .orElseThrow(() -> new EntityNotFoundException(msg("curso.error.noexiste")));
    CursoResponse resp = modelMapper.map(curso, CursoResponse.class);
    if(curso.getMateriales()!=null){
        resp.setMateriales(curso.getMateriales().stream().map(m -> MaterialApoyoResponse.builder()
            .id(m.getId())
            .nombre(m.getNombre())
            .descripcion(m.getDescripcion())
            .enlace(m.getEnlace())
            .build()).toList());
    }
    return resp;
    }

    @Override
    @Transactional
    public void eliminarCurso(Long id) {
        // Asegurar existencia
        cursoRepository.findCursoById(id); 
        //faltan mas validaciones (matriculas asociadas)
        cursoRepository.deleteCurso(id);
    }

    @Override
    @Transactional
    public CursoResponse actualizarCurso(Long id, CursoRequest request) {
        // Validaciones similares a crear: periodo activo, asignatura activa, docentes activos, unicidad
    PeriodoAcademico periodo = periodoAcademicoRepository.findPeriodoActivo()
        .orElseThrow(() -> new EntityNotFoundException(msg("curso.error.periodo.activo.noexiste")));
        Long periodoActivoId = periodo.getId();

        // Verificar que el curso a actualizar pertenece al período ACTIVO
        Curso cursoActual = cursoRepository.findCursoById(id)
            .orElseThrow(() -> new EntityNotFoundException(msg("curso.error.noexiste")));
        if (cursoActual.getPeriodo() == null || !periodoActivoId.equals(cursoActual.getPeriodo().getId())) {
            throw new IllegalArgumentException(msg("curso.error.curso.no.pertenece.periodo.activo"));
        }

        Asignatura asignatura = cursoRepository.findAsignaturaById(request.getAsignaturaId())
                .orElseThrow(() -> new EntityNotFoundException(msg("curso.error.asignatura.noexiste")));
        if (asignatura.getEstado() == null || !asignatura.getEstado()) {
            throw new IllegalArgumentException(msg("curso.error.asignatura.inactiva"));
        }

        List<Long> idsSolicitados = request.getDocentesIds();
        List<Docente> docentesList = cursoRepository.findDocentesByIds(idsSolicitados);
        var idsEncontrados = docentesList.stream().map(Docente::getId).collect(Collectors.toSet());
        List<Long> faltantes = idsSolicitados.stream().filter(d -> !idsEncontrados.contains(d)).toList();
        if (!faltantes.isEmpty()) {
            throw new IllegalArgumentException(msg("curso.error.docentes.noencontrados", faltantes));
        }
        List<Long> inactivos = docentesList.stream()
                .filter(d -> d.getEstado() == null || !"ACTIVO".equalsIgnoreCase(d.getEstado()))
                .map(Docente::getId)
                .toList();
        if (!inactivos.isEmpty()) {
            throw new IllegalArgumentException(msg("curso.error.docentes.inactivos", inactivos));
        }

        // Unicidad en periodo activo (ignorando el mismo id)
        boolean existeEnActivo = cursoRepository.existsByGrupoAndPeriodoIdAndAsignaturaId(
                request.getGrupo(), periodoActivoId, request.getAsignaturaId());
        if (existeEnActivo) {
            boolean esMismo = cursoRepository.findAllCursos().stream()
                .anyMatch(c -> c.getId().equals(id));
            if (!esMismo) {
                throw new IllegalArgumentException(msg("curso.error.unicidad.grupo_asignatura_periodo"));
            }
        }

        Set<Docente> docentes = Set.copyOf(docentesList);

        // Materiales (similar a crear)
        Set<MaterialApoyo> materiales = Set.of();
        if(request.getMaterialApoyoIds()!=null && !request.getMaterialApoyoIds().isEmpty()){
            List<MaterialApoyo> mats = materialApoyoRepository.findAllByIds(request.getMaterialApoyoIds());
            var encontrados = mats.stream().map(MaterialApoyo::getId).collect(Collectors.toSet());
            List<Long> faltantesMat = request.getMaterialApoyoIds().stream().filter(mid -> !encontrados.contains(mid)).toList();
            if(!faltantesMat.isEmpty()){
                throw new IllegalArgumentException(msg("curso.error.materiales.noencontrados", faltantesMat));
            }
            materiales = Set.copyOf(mats);
        }

        Curso curso = Curso.builder()
                .id(id)
                .grupo(request.getGrupo())
                .periodo(periodo)
                .asignatura(asignatura)
                .docentes(docentes)
                .materiales(materiales)
                .horario(request.getHorario())
                .salon(request.getSalon())
                .observacion(request.getObservacion())
                .build();
    Curso actualizado = cursoRepository.saveCurso(curso);
    CursoResponse resp = modelMapper.map(actualizado, CursoResponse.class);
    if(actualizado.getMateriales()!=null){
        resp.setMateriales(actualizado.getMateriales().stream().map(m -> MaterialApoyoResponse.builder()
            .id(m.getId())
            .nombre(m.getNombre())
            .descripcion(m.getDescripcion())
            .enlace(m.getEnlace())
            .build()).toList());
    }
    return resp;
    }

    @Override
    public boolean existeCurso(String grupo, Long asignaturaId) {
        Long periodoId = periodoAcademicoRepository.findPeriodoActivo()
                .map(PeriodoAcademico::getId)
                .orElse(null);
        if (periodoId == null) return false;
        return cursoRepository.existsByGrupoAndPeriodoIdAndAsignaturaId(grupo, periodoId, asignaturaId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeCursoPorId(Long id) {
        try {
            cursoRepository.findCursoById(id).orElseThrow();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponse> obtenerTodosLosCursos() {
        List<Curso> cursos = cursoRepository.findAllCursos();
        return cursos.stream().map(c -> {
            CursoResponse resp = modelMapper.map(c, CursoResponse.class);
            if(c.getMateriales()!=null){
                resp.setMateriales(c.getMateriales().stream().map(m -> MaterialApoyoResponse.builder()
                        .id(m.getId())
                        .nombre(m.getNombre())
                        .descripcion(m.getDescripcion())
                        .enlace(m.getEnlace())
                        .build()).toList());
            }
            return resp;
        }).toList();
    }

    

    private String msg(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
   

}
