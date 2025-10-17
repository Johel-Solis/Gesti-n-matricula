package unicauca.edu.co.ms_gestion_maticula.app.ports.In;

import java.util.List;

import unicauca.edu.co.ms_gestion_maticula.app.domain.request.MaterialApoyoRequest;
import unicauca.edu.co.ms_gestion_maticula.app.domain.response.MaterialApoyoResponse;

public interface MaterialApoyoService {
    MaterialApoyoResponse crear(MaterialApoyoRequest request);
    MaterialApoyoResponse actualizar(Long id, MaterialApoyoRequest request);
    void eliminar(Long id);
    MaterialApoyoResponse obtener(Long id);
    List<MaterialApoyoResponse> listar();
}
