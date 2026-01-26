package unicauca.edu.co.ms_gestion_maticula.domain.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import unicauca.edu.co.ms_gestion_maticula.domain.enums.MatriculaEstado;
import unicauca.edu.co.ms_gestion_maticula.domain.model.AreaFormacion;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Asignatura;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Curso;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Docente;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Estudiante;
import unicauca.edu.co.ms_gestion_maticula.domain.model.PeriodoAcademico;
import unicauca.edu.co.ms_gestion_maticula.domain.model.MaterialApoyo;
import unicauca.edu.co.ms_gestion_maticula.domain.request.CursoRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.request.CursoReportRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.response.AsignaturaResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.CursoResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.DocenteResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.EstudianteResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MaterialApoyoResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.ReportCursoDto;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.MatriculaJpaAdapter;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.In.CusoService;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.In.MatriculaService;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.out.CursoRepository;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.out.PeriodoAcademicoRepository;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.out.MaterialApoyoRepository;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

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

    private final MatriculaService matriculaService;

    private final MatriculaJpaAdapter matriculaRepository;

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
        if (cursoRepository.existsByGrupoAndPeriodoIdAndAsignaturaId(request.getGrupo(), periodoActivoId,
                request.getAsignaturaId())) {
            throw new IllegalArgumentException(msg("curso.error.unicidad.grupo_asignatura_periodo"));
        }

        // 5) Construir y guardar curso
        Set<Docente> docentes = new HashSet<>(docentesList);

        // 6) Materiales de apoyo (opcionales) - validar existencia si se enviaron
        Set<MaterialApoyo> materiales = new HashSet<>();
        if (request.getMaterialApoyoIds() != null && !request.getMaterialApoyoIds().isEmpty()) {
            List<MaterialApoyo> mats = materialApoyoRepository.findAllByIds(request.getMaterialApoyoIds());
            var encontrados = mats.stream().map(MaterialApoyo::getId).collect(Collectors.toSet());
            List<Long> faltantesMat = request.getMaterialApoyoIds().stream().filter(mid -> !encontrados.contains(mid))
                    .toList();
            if (!faltantesMat.isEmpty()) {
                throw new IllegalArgumentException(msg("curso.error.materiales.noencontrados", faltantesMat));
            }
            materiales.addAll(mats);
        }

        Curso curso = Curso.builder()
                .grupo(request.getGrupo())
                .periodo(periodo)
                .asignatura(asignatura)
                .docentes(docentes.stream().collect(Collectors.toList()))
                .materiales(materiales.stream().collect(Collectors.toList()))
                .horario(request.getHorario())
                .salon(request.getSalon())
                .observacion(request.getObservacion())
                .estado(true)
                .build();

        Curso result = cursoRepository.saveCurso(curso);
        CursoResponse resp = modelMapper.map(result, CursoResponse.class);
        if (result.getMateriales() != null) {
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
        if (curso.getMateriales() != null) {
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
        // faltan mas validaciones (matriculas asociadas)
        cursoRepository.deleteCurso(id);
    }

    @Override
    @Transactional
    public CursoResponse actualizarCurso(Long id, CursoRequest request) {
        // Validaciones similares a crear: periodo activo, asignatura activa, docentes
        // activos, unicidad
      
        // Verificar que el curso a actualizar pertenece al período ACTIVO
        Curso cursoActual = cursoRepository.findCursoById(id)
                .orElseThrow(() -> new EntityNotFoundException(msg("curso.error.noexiste")));

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
                request.getGrupo(), cursoActual.getPeriodo().getId(), request.getAsignaturaId());
        if (existeEnActivo) {
            boolean esMismo = cursoRepository
                    .findAllCursos(Long.valueOf(asignatura.getAreaFormacion()), asignatura.getId(), cursoActual.getPeriodo().getId())
                    .stream()
                    .anyMatch(c -> c.getId().equals(id));
            if (!esMismo) {
                throw new IllegalArgumentException(msg("curso.error.unicidad.grupo_asignatura_periodo"));
            }
        }

        Set<Docente> docentes = Set.copyOf(docentesList);

        // Materiales (similar a crear)
        Set<MaterialApoyo> materiales = Set.of();
        if (request.getMaterialApoyoIds() != null && !request.getMaterialApoyoIds().isEmpty()) {
            List<MaterialApoyo> mats = materialApoyoRepository.findAllByIds(request.getMaterialApoyoIds());
            var encontrados = mats.stream().map(MaterialApoyo::getId).collect(Collectors.toSet());
            List<Long> faltantesMat = request.getMaterialApoyoIds().stream().filter(mid -> !encontrados.contains(mid))
                    .toList();
            if (!faltantesMat.isEmpty()) {
                throw new IllegalArgumentException(msg("curso.error.materiales.noencontrados", faltantesMat));
            }
            materiales = Set.copyOf(mats);
        }

        Curso curso = Curso.builder()
                .id(id)
                .grupo(request.getGrupo())
                .periodo(cursoActual.getPeriodo())
                .asignatura(asignatura)
                .docentes(docentes.stream().collect(Collectors.toList()))
                .materiales(materiales.stream().collect(Collectors.toList()))
                .horario(request.getHorario())
                .salon(request.getSalon())
                .observacion(request.getObservacion())
                .build();
        Curso actualizado = cursoRepository.saveCurso(curso);
        CursoResponse resp = modelMapper.map(actualizado, CursoResponse.class);
        if (actualizado.getMateriales() != null) {
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
        if (periodoId == null)
            return false;
        return cursoRepository.existsByGrupoAndPeriodoIdAndAsignaturaId(grupo, periodoId, asignaturaId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeCursoPorId(Long id) {
        try {
            Optional<Curso> cursoOpt = cursoRepository.findCursoById(id);
            if (cursoOpt.isEmpty()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponse> obtenerTodosLosCursos(Long idArea, Long idAsignatura, Long idPeriodo) {
        List<Curso> cursos = cursoRepository.findAllCursos(idArea, idAsignatura, idPeriodo);
        return cursos.stream().map(c -> {
            CursoResponse resp = modelMapper.map(c, CursoResponse.class);
            if (c.getMateriales() != null) {
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

    @Override
    public List<AsignaturaResponse> obtenerAsignaturasPorEstado(Long idArea) {
        List<Asignatura> asignaturas = cursoRepository.findAsignaturasByStatus(true, idArea);
        return asignaturas.stream()
                .map(a -> modelMapper.map(a, AsignaturaResponse.class))
                .toList();
    }

    @Override
    public List<DocenteResponse> obtenerDocentesPorAsignaturaId(Long asignaturaId) {
        List<Docente> docentes = cursoRepository.findDocentesByAsignaturaId(asignaturaId);
        return docentes.stream()
                .map(Docente::toResponse)
                .toList();
    }

    @Override
    public List<AreaFormacion> obtenerAreasFormacion() {
        return cursoRepository.findAllAreasFormacion();
    }

    @Override
    public List<CursoResponse> obtenerCursosDisponibles(Long idEstudiante, Long idArea) {
        PeriodoAcademico periodo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new EntityNotFoundException(msg("curso.error.periodo.activo.noexiste")));

        List<Asignatura> asignaturasDisponibles = matriculaService
                .obtenerAsignaturasDisponiblesporEstudiante(idEstudiante);

        List<Long> asignaturasIds = new ArrayList<>();
        if (idArea != null) {
            asignaturasIds = asignaturasDisponibles.stream()
                    .filter(a -> a.getAreaFormacion() != null
                            && a.getAreaFormacion().equals(Integer.parseInt(idArea.toString())))
                    .map(Asignatura::getId)
                    .toList();
        } else {
            asignaturasIds = asignaturasDisponibles.stream()
                    .map(Asignatura::getId)
                    .toList();
        }

        List<Curso> cursos = cursoRepository.getCursosByAsignaturaIds(asignaturasIds, periodo.getId());

        return cursos.stream()
                .map(c -> modelMapper.map(c, CursoResponse.class))
                .toList();
    }

    @Override
    public List<EstudianteResponse> obtenerEstudiantesDisponiblesPorCursoAsignatura(Long asignaturaId) {
        Optional<Asignatura> asignatura = cursoRepository.findAsignaturaById(asignaturaId);
        if (!asignatura.isPresent()) {
            throw new EntityNotFoundException("Asignatura no encontrado con ID: " + asignaturaId);
        }

        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new IllegalArgumentException("No hay periodo académico activo"));

        List<Estudiante> estudiantes = cursoRepository.findEstudiantesDisponiblesPorAsignatura(asignaturaId,
                periodoActivo.getId());

        return estudiantes.stream()
                .map(c -> modelMapper.map(c, EstudianteResponse.class))
                .toList();

    }

    @Override
    public List<EstudianteResponse> obtenerEstudiantesActivos() {
        List<Estudiante> estudiantes = cursoRepository.getEstudiantesActivos();

        return estudiantes.stream()
                .map(e -> modelMapper.map(e, EstudianteResponse.class))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generarReporteCursos(CursoReportRequest request, String formato) {
        PeriodoAcademico periodo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new EntityNotFoundException(msg("curso.error.periodo.activo.noexiste")));

        List<Curso> cursos = cursoRepository.findAllCursos(null, null, periodo.getId()).stream()
                .filter(Curso::isEstado)
                .toList();

        if (request != null) {
            if (request.getAsignaturaIds() != null && !request.getAsignaturaIds().isEmpty()) {
                cursos = cursos.stream()
                        .filter(c -> c.getAsignatura() != null
                                && request.getAsignaturaIds().contains(c.getAsignatura().getId()))
                        .toList();
            }
            if (request.getCursosIds() != null && !request.getCursosIds().isEmpty()) {
                cursos = cursos.stream()
                        .filter(c -> request.getCursosIds().contains(c.getId()))
                        .toList();
            }
        }

        List<ReportCursoDto> data = cursos.stream()
                .map(this::toReportCursoDto)
                .toList();
        
        

        try (InputStream reportStream = getClass().getResourceAsStream("/Reportes/cursos.jasper");
             InputStream logoStream = getClass().getResourceAsStream("/image/logo-unicauca.png")) {
            if (reportStream == null) {
                throw new IllegalArgumentException("No se encontro el reporte cursos.jasper");
            }
            if (logoStream == null) {
                throw new IllegalArgumentException("No se encontro el logo para el reporte");
            }
            
            Map<String, Object> params = new HashMap<>();
           params.put("logoUnicauca", new BufferedInputStream(logoStream));
            params.put("fecha_periodo", periodo.getFechaInicio().format(DateTimeFormatter.ofPattern("dd/MM/yy")) + " - " + periodo.getFechaFin().format(DateTimeFormatter.ofPattern("dd/MM/yy")));
            params.put("tag_periodo", periodo.getTagPeriodo()+"");
            params.put("ds", new JRBeanArrayDataSource(data.toArray()));
            JasperPrint print = JasperFillManager.fillReport(reportStream, params,
                   new JRBeanArrayDataSource(data.toArray()));

            if (isExcelFormat(formato)) {
                return exportXlsx(print);
            }
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException e) {
            
            throw new IllegalStateException("Error generando el reporte de cursos", e);
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el reporte de cursos", e);
        }
    }

    private ReportCursoDto toReportCursoDto(Curso curso) {
        return ReportCursoDto.builder()
                .grupo(curso.getGrupo())
                .asignatura(curso.getAsignatura() != null ? curso.getAsignatura().getNombre() : "")
                .docentes(formatDocentes(curso.getDocentes()))
                .horario(curso.getHorario() != null ? curso.getHorario() : "")
                .salon(curso.getSalon() != null ? curso.getSalon() : "")
                .creditos(curso.getAsignatura().getCreditos().toString())
                .build();
    }

    private String formatDocentes(List<Docente> docentes) {
        if (docentes == null || docentes.isEmpty()) {
            return "Sin docentes";
        }
        return docentes.stream()
                .map(this::formatDocenteNombre)
                .collect(Collectors.joining(", "));
    }

    private String formatDocenteNombre(Docente docente) {
        if (docente == null) {
            return "";
        }
        if (docente.getPersona() == null) {
            return docente.getCodigo() != null ? docente.getCodigo() : "";
        }
        String nombre = docente.getPersona().getNombre() != null ? docente.getPersona().getNombre() : "";
        String apellido = docente.getPersona().getApellido() != null ? docente.getPersona().getApellido() : "";
        String full = (nombre + " " + apellido).trim();
        return full.isEmpty() ? (docente.getCodigo() != null ? docente.getCodigo() : "") : full;
    }

    private boolean isExcelFormat(String formato) {
        if (formato == null) {
            return false;
        }
        String normalized = formato.trim().toLowerCase();
        return normalized.equals("xlsx") || normalized.equals("excel");
    }

    private byte[] exportXlsx(JasperPrint print) throws JRException {
        JRXlsxExporter exporter = new JRXlsxExporter();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        exporter.setExporterInput(new SimpleExporterInput(print));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setDetectCellType(true);
        configuration.setCollapseRowSpan(false);
        exporter.setConfiguration(configuration);
        exporter.exportReport();
        return outputStream.toByteArray();
    }

    @Override
    public List<CursoResponse> obtenerCursosPorMatriculaAprobada() {
        PeriodoAcademico periodoActivo = periodoAcademicoRepository.findPeriodoActivo()
                .orElseThrow(() -> new RuntimeException("No hay un periodo academico activo"));
        List<Curso> cursos = cursoRepository.getCursosByPeriodoIdAndEstadoMatricula(periodoActivo.getId(),MatriculaEstado.APROBADA.name());
        return cursos.stream()
                .map(c -> modelMapper.map(c, CursoResponse.class))
                .toList();
    }

}
