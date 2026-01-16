package unicauca.edu.co.ms_gestion_maticula.domain.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.In.EstudianteDocenteService;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.out.EstudianteDocenteRepository;
import unicauca.edu.co.ms_gestion_maticula.domain.response.DocenteResponse;

@Service
@RequiredArgsConstructor
public class EstudianteDocenteServiceImpl implements EstudianteDocenteService {

    @Autowired
    private final EstudianteDocenteRepository estudianteDocenteRepo;

    @Autowired
    private final ModelMapper modelMapper;

    @Override
    public List<DocenteResponse> getDirectores() {
        List<DocenteResponse> directores = estudianteDocenteRepo.getDirectores().stream()
                .map(director -> modelMapper.map(director, DocenteResponse.class))
                .toList();
        return directores;
    }

}
