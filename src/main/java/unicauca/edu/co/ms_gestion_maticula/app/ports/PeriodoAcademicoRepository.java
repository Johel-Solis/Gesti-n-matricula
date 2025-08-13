package unicauca.edu.co.ms_gestion_maticula.app.ports;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import unicauca.edu.co.ms_gestion_maticula.app.domain.model.PeriodoAcademico;

public interface PeriodoAcademicoRepository  {

    PeriodoAcademico save(PeriodoAcademico periodo);
    void deleteById(Long id);
    Optional<PeriodoAcademico> findById(Long id);
    List<PeriodoAcademico> findAll();
    Optional<PeriodoAcademico> findPeriodoActivo();
    List<PeriodoAcademico> findByFechaSuperpuesta(LocalDate inicio, LocalDate fin);

}
