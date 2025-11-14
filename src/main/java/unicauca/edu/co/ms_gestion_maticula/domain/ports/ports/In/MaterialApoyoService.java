package unicauca.edu.co.ms_gestion_maticula.domain.ports.ports.In;

import java.util.List;

import unicauca.edu.co.ms_gestion_maticula.domain.request.MaterialApoyoRequest;
import unicauca.edu.co.ms_gestion_maticula.domain.response.MaterialApoyoResponse;

public interface MaterialApoyoService {
    MaterialApoyoResponse crear(MaterialApoyoRequest request);
    MaterialApoyoResponse actualizar(Long id, MaterialApoyoRequest request);
    void eliminar(Long id);
    MaterialApoyoResponse obtener(Long id);
    List<MaterialApoyoResponse> listar();
}
