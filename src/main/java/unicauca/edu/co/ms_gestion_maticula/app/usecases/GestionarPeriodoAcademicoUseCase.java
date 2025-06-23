package unicauca.edu.co.ms_gestion_maticula.app.usecases;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import unicauca.edu.co.ms_gestion_maticula.domain.enums.PeriodoEstadoEnum;
import unicauca.edu.co.ms_gestion_maticula.domain.model.PeriodoAcademico;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.PeriodoAcademicoRepository;

public class GestionarPeriodoAcademicoUseCase {
    private final PeriodoAcademicoRepository repository;

    public GestionarPeriodoAcademicoUseCase(PeriodoAcademicoRepository repository) {
        this.repository = repository;
    }

    public PeriodoAcademico crearPeriodo(PeriodoAcademico nuevo) {
        nuevo.setEstado(PeriodoEstadoEnum.ACTIVO.name());
        validarPeriodo(nuevo);
        return repository.save(nuevo);
    }

    public PeriodoAcademico actualizar(Long id, PeriodoAcademico actualizado) {
         repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Periodo no encontrado"));

        actualizado.setId(id);
        validarPeriodo(actualizado);
        return repository.save(actualizado);
    }

    public List<PeriodoAcademico> listar() {
        return repository.findAll();
    }

    public PeriodoAcademico obtenerPorId(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Periodo no encontrado"));
    }

    public void eliminar(Long id) {
        repository.deleteById(id);
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

}
