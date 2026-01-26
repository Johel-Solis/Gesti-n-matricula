package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.enums.EstadoEstudianteMaestria;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Docente;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Estudiante;
import unicauca.edu.co.ms_gestion_maticula.domain.model.TutorEstudiante;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.out.EstudianteDocenteRepository;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.EstudianteEntity;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository.DocenteJpaRepository;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository.EstudianteJpaRepository;

@Component
@RequiredArgsConstructor
public class EstudianteDocenteJpaAdapter implements EstudianteDocenteRepository {

    private final EstudianteJpaRepository estudianteRepo;
    private final DocenteJpaRepository docenteRepo;

    @Override
    public Optional<Estudiante> getEstudianteById(Long estudianteId) {
        return estudianteRepo.findById(estudianteId)
                .map(EstudianteEntity::toDomain);
    }

    @Override
    public Optional<Estudiante> getEstudianteByIdAndEstado(Long estudianteId, EstadoEstudianteMaestria estado) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEstudianteByIdAndEstado'");
    }

    @Override
    public List<TutorEstudiante> getDirectores() {

        List<TutorEstudiante> directores = docenteRepo.getDirectores().stream()
                .map(director -> new TutorEstudiante().fromEntity(director))
                .toList();
        return directores;
    }

    @Override
    public List<Estudiante> findEstudiantesDisponiblesPorAsignatura(Long asignaturaId, Long periodoId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findEstudiantesDisponiblesPorAsignatura'");
    }

    @Override
    public List<Estudiante> getEstudiantesActivos() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEstudiantesActivos'");
    }

    @Override
    public List<Docente> findDocentesByAsignaturaId(Long asignaturaId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findDocentesByAsignaturaId'");
    }

    @Override
    public List<Docente> findDocentesByIds(List<Long> docenteIds) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findDocentesByIds'");
    }

    @Override
    public List<Estudiante> findEstudiantesByTutor(Long tutorId) {
       List<Estudiante> estudiantes = estudianteRepo.findByTutor(tutorId).stream()
                .map(Estudiante::fromEntity)
                .toList();
        return estudiantes;
    }

    @Override
    public List<Estudiante> findEstudiantesMatriculados(Long periodoId, String estadoMatricula) {
        List<Estudiante> estudiantes = estudianteRepo.findByPeriodo(periodoId, estadoMatricula).stream()
                .map(Estudiante::fromEntity)
                .toList();
        return estudiantes;
    }

    @Override
    public List<Docente> findTutoresByEstudiante(Long estudianteId) {
        return docenteRepo.findTutoresByEstudiante(estudianteId).stream()
                .map(Docente::fromEntity)
                .toList();
    }

    @Override
    public Optional<Docente> findDocenteByEmail(String email) {
        return docenteRepo.findByEmail(email)
                .map(Docente::fromEntity);
    }

    @Override
    public boolean isTutorDeEstudiante(Long tutorId, String codigoEstudiante) {
        return estudianteRepo.isTutorDeEstudiante(tutorId, codigoEstudiante);
    }

    @Override
    public List<Docente> getDocentesActivos() {
        return docenteRepo.getDocentesActivos().stream()
                .map(Docente::fromEntity)
                .toList();
    }

    @Override
    public boolean existsEstudianteActivoById(Long estudianteId) {
        return estudianteRepo.existsEstudianteActivoById(estudianteId, EstadoEstudianteMaestria.ACTIVO);
    }

}
