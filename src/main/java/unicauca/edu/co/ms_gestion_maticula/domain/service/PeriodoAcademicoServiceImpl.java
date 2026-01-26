package unicauca.edu.co.ms_gestion_maticula.domain.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.enums.PeriodoEstadoEnum;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Curso;
import unicauca.edu.co.ms_gestion_maticula.domain.model.PeriodoAcademico;
import unicauca.edu.co.ms_gestion_maticula.domain.request.PeriodoAcademicoRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.response.PeriodoFechaResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.PeriodoAcademicoResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.In.PeriodoAcademicoService;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.out.CursoRepository;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.out.PeriodoAcademicoRepository;

@Service
@RequiredArgsConstructor
public class PeriodoAcademicoServiceImpl implements PeriodoAcademicoService {
    @Autowired
    private final PeriodoAcademicoRepository repository;
    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private final CursoRepository cursoRepository;

    @Qualifier("messageResourceMatricula")
	MessageSource messageSource;    

    @Override
    @Transactional
    public PeriodoAcademicoResponse crearPeriodo(PeriodoAcademicoRequest nuevo) {
        PeriodoAcademico periodo = modelMapper.map(nuevo, PeriodoAcademico.class);
        periodo.setEstado(PeriodoEstadoEnum.ACTIVO.name());
        validarPeriodo(periodo);
        PeriodoAcademico periodoResult=repository.save(periodo);
        return modelMapper.map(periodoResult, PeriodoAcademicoResponse.class);
    }

    @Override
    public PeriodoAcademicoResponse actualizar(Long id, PeriodoAcademicoRequest actualizado) {
         if(!repository.existsById(id)) {
            throw new IllegalArgumentException("Periodo no encontrado");
         }

        PeriodoAcademico periodo = modelMapper.map(actualizado, PeriodoAcademico.class);

        periodo.setId(id);
        validarPeriodo(periodo);
        PeriodoAcademico periodoResult = repository.save(periodo);
        return modelMapper.map(periodoResult, PeriodoAcademicoResponse.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PeriodoAcademicoResponse> listar() {
        List<PeriodoAcademico> periodos = repository.findAll();
        return modelMapper.map(periodos, new TypeToken<List<PeriodoAcademicoResponse>>(){}.getType());
    }

    @Override
    @Transactional(readOnly = true)
    public PeriodoAcademicoResponse obtenerPorId(Long id) {
        PeriodoAcademico periodo = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Periodo no encontrado"));
        return modelMapper.map(periodo, PeriodoAcademicoResponse.class);
    }

    @Override
    public void eliminar(Long id) {
        PeriodoAcademico periodo = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Periodo no encontrado"));
        List<Curso> cursosPeriodo = cursoRepository.findAllCursos(null,null,periodo.getId() );
        if (cursosPeriodo.size()>0) {
            throw new IllegalArgumentException("No se puede eliminar el período porque tiene cursos asignados.");
        }
        
        repository.deleteById(id);
    }

    @Override
    public PeriodoFechaResponse validarFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        PeriodoFechaResponse resultado = new PeriodoFechaResponse();

        // Validar que fechaInicio no sea después que fechaFin
        if (fechaInicio.isAfter(fechaFin)) {
            resultado.setDisponible(false);
            resultado.setMensaje("La fecha de inicio no puede ser mayor que la fecha de fin.");
            return resultado;
        }

        // Validar que las fechas no se superpongan con períodos existentes
        List<PeriodoAcademico> superpuestos = repository.findByFechaSuperpuesta(fechaInicio, fechaFin);
        
        if (!superpuestos.isEmpty()) {
            resultado.setDisponible(false);
            resultado.setMensaje("Las fechas se superponen con un período académico existente.");
            return resultado;
        }

        resultado.setDisponible(true);
        resultado.setMensaje("Las fechas están disponibles para crear un nuevo período académico.");
        return resultado;
    }

    @Override
    public PeriodoAcademicoResponse obtenerPeriodoActivo() {
        Optional<PeriodoAcademico> periodoOpt = repository.findPeriodoActivo();
        if (periodoOpt.isEmpty()) {
            throw new IllegalArgumentException("No hay un período académico activo.");
        }
        return modelMapper.map(periodoOpt.get(), PeriodoAcademicoResponse.class);
    }

    private void validarPeriodo(PeriodoAcademico p) {
        // 1. Fecha de inicio no puede ser después que la fecha de fin
        if (p.getFechaInicio().isAfter(p.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser mayor que la fecha de fin.");
        }

        // 2. Fecha de fin de matrícula debe estar dentro del rango del período
        if (p.getFechaFinMatricula().isBefore(p.getFechaInicio()) || 
            p.getFechaFinMatricula().isAfter(p.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de fin de matrícula debe estar entre la fecha de inicio y la fecha de fin del período.");
        }

        // 3. Validar que no se solape con otro período (excepto consigo mismo en caso de update)
        List<PeriodoAcademico> superpuestos = repository.findByFechaSuperpuesta(p.getFechaInicio(), p.getFechaFin());
        boolean existeSolapamiento = superpuestos.stream()
            .anyMatch(existing -> !existing.getId().equals(p.getId()));

        if (!superpuestos.isEmpty() && existeSolapamiento) {
            throw new IllegalArgumentException("El período se superpone con otro período académico existente.");
        }

        // 4. Validar que no haya otro período activo
        if ("ACTIVO".equalsIgnoreCase(p.getEstado())) {
            Optional<PeriodoAcademico> activo = repository.findPeriodoActivo();
            if (activo.isPresent() && !activo.get().getId().equals(p.getId())) {
                throw new IllegalArgumentException("Ya existe un período académico activo.");
            }
        }

        // 5. No permitir finalizar antes de fecha fin de matrícula
        if ("FINALIZADO".equalsIgnoreCase(p.getEstado())) {
            if (LocalDate.now().isBefore(p.getFechaFinMatricula())) {
                throw new IllegalArgumentException("No se puede cerrar el período antes de la fecha de finalización de matrícula.");
            }
        }
}

    @Override
    public PeriodoAcademicoResponse precargarCursosDesdePeriodo(Long idPeriodo, Long idPeriodoPrecarga) {
       PeriodoAcademico periodo = repository.findById(idPeriodo)
            .orElseThrow(() -> new IllegalArgumentException("Periodo no encontrado"));
         PeriodoAcademico periodoPrecarga = repository.findById(idPeriodoPrecarga)
            .orElseThrow(() -> new IllegalArgumentException("Periodo de precarga no encontrado"));

        if (!(periodo.getEstado().equals(PeriodoEstadoEnum.ACTIVO.name()))) {
            throw new IllegalArgumentException("El período debe estar activo para precargar cursos.");
        }

        List<Curso> cursosPeriodo = cursoRepository.findAllCursos(null,null,periodo.getId() );

        if (cursosPeriodo.size()>0) {
            throw new IllegalArgumentException("El período ya tiene cursos asignados, no se puede precargar.");
            
        }else{
            List<Curso> cursosAPrecargar = cursoRepository.findAllCursos(null,null,periodoPrecarga.getId() );

            if (cursosAPrecargar.size()<=0) {
                throw new IllegalArgumentException("El período de precarga no tiene cursos para copiar.");
            }
            for (Curso c : cursosAPrecargar) {
                Curso nuevoCurso = new Curso();
                nuevoCurso.setAsignatura(c.getAsignatura());
                nuevoCurso.setDocentes(c.getDocentes());
                nuevoCurso.setGrupo(c.getGrupo());
                nuevoCurso.setHorario(c.getHorario());
                nuevoCurso.setSalon(c.getSalon());
                nuevoCurso.setPeriodo(periodo);
                cursoRepository.saveCurso(nuevoCurso);
            }
            PeriodoAcademico periodoResponse = repository.findById(idPeriodo)
            .orElseThrow(() -> new IllegalArgumentException("Periodo no encontrado"));
            return modelMapper.map(periodoResponse, PeriodoAcademicoResponse.class);
        }


    }

}
