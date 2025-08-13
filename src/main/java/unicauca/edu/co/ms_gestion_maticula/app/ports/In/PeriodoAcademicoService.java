package unicauca.edu.co.ms_gestion_maticula.app.ports.In;

import java.time.LocalDate;
import java.util.List;


import unicauca.edu.co.ms_gestion_maticula.app.domain.request.PeriodoAcademicoRequest;
import unicauca.edu.co.ms_gestion_maticula.app.domain.response.PeriodoFechaResponse;
import unicauca.edu.co.ms_gestion_maticula.app.domain.response.PeriodoAcademicoResponse;

public interface PeriodoAcademicoService {
  

    public PeriodoAcademicoResponse crearPeriodo(PeriodoAcademicoRequest nuevo) ;
    public PeriodoAcademicoResponse actualizar(Long id, PeriodoAcademicoRequest actualizado) ;

    public List<PeriodoAcademicoResponse> listar() ;

    public PeriodoAcademicoResponse obtenerPorId(Long id) ;

    public void eliminar(Long id);

    public PeriodoFechaResponse validarFechas(LocalDate fechaInicio, LocalDate fechaFin);
       

  




}
