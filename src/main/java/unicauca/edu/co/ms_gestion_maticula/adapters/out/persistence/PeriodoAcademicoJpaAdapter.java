package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.PeriodoAcademicoEntity;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository.PeriodoJpaRepository;
import unicauca.edu.co.ms_gestion_maticula.app.domain.model.PeriodoAcademico;
import unicauca.edu.co.ms_gestion_maticula.app.ports.out.PeriodoAcademicoRepository;


@Component
public class PeriodoAcademicoJpaAdapter implements PeriodoAcademicoRepository {

    private final PeriodoJpaRepository jpaRepository;

    public PeriodoAcademicoJpaAdapter(PeriodoJpaRepository repo) {
        this.jpaRepository = repo;
    }

    @Override
    public PeriodoAcademico save(PeriodoAcademico p) {
        return jpaRepository.save(p.toEntity()).toDomain();
    }

    @Override
    public Optional<PeriodoAcademico> findById(Long id) {
        return jpaRepository.findById(id).map(PeriodoAcademicoEntity::toDomain);
    }
    @Override
    public Optional<PeriodoAcademico> findByState(String estado) {
        return jpaRepository.findByEstado(estado).map(PeriodoAcademicoEntity::toDomain);
    }

    @Override
    public List<PeriodoAcademico> findAll() {
        return jpaRepository.findAll().stream()
                .map(PeriodoAcademicoEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<PeriodoAcademico> findPeriodoActivo() {
        return jpaRepository.findByEstado("ACTIVO").map(PeriodoAcademicoEntity::toDomain);
    }

    @Override
    public List<PeriodoAcademico> findByFechaSuperpuesta(LocalDate inicio, LocalDate fin) {
        return jpaRepository.findByFechasSuperpuestas(inicio, fin).stream()
                .map(PeriodoAcademicoEntity::toDomain)
                .toList();
    }
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

}
