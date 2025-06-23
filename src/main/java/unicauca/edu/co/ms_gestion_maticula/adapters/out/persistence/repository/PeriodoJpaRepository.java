package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.PeriodoAcademicoEntity;


@Repository
public interface PeriodoJpaRepository extends JpaRepository<PeriodoAcademicoEntity,Long>{

    @Query("SELECT p FROM PeriodoAcademicoEntity p WHERE p.estado = :estado")
    Optional<PeriodoAcademicoEntity> findByEstado(@Param("estado") String estado);

    @Query("SELECT p FROM PeriodoAcademicoEntity p WHERE " +
           "(p.fechaInicio BETWEEN :inicio AND :fin OR " +
           "p.fechaFin BETWEEN :inicio AND :fin OR " +
           ":inicio BETWEEN p.fechaInicio AND p.fechaFin OR " +
           ":fin BETWEEN p.fechaInicio AND p.fechaFin)")
    List<PeriodoAcademicoEntity> findByFechasSuperpuestas(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

}
