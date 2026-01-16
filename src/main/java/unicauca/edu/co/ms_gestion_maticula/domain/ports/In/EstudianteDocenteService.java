package unicauca.edu.co.ms_gestion_maticula.domain.ports.In;

import java.util.List;

import unicauca.edu.co.ms_gestion_maticula.domain.response.DocenteResponse;

public interface EstudianteDocenteService {

    public List<DocenteResponse> getDirectores();
    
    
}
