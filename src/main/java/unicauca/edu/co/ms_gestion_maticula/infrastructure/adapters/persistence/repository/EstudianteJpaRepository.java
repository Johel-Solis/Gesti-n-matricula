package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import unicauca.edu.co.ms_gestion_maticula.domain.enums.EstadoEstudianteMaestria;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Estudiante;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.EstudianteEntity;

public interface EstudianteJpaRepository extends JpaRepository<EstudianteEntity, Long> {

    @Query("SELECT e FROM EstudianteEntity e WHERE e.id = :id AND e.informacionMaestria.estadoMaestria = :estado")
    Optional<EstudianteEntity> getEstudianteByIdAndEstado(Long id, EstadoEstudianteMaestria estado);

    @Query("""
            SELECT e
            FROM EstudianteEntity e
            WHERE e.informacionMaestria.estadoMaestria = 'ACTIVO'
            AND NOT EXISTS (
                SELECT 1
                FROM MatriculaCalificacion mc
                WHERE mc.matricula.estudiante.id = e.id
                  AND mc.asignatura.id = :idAsignatura
                  AND mc.esDefinitiva = true
                  AND mc.nota >= :notaMinima
            )
            AND NOT EXISTS (
                SELECT 1
                FROM MatriculaEntity m
                WHERE m.estudiante.id = e.id
                  AND m.curso.asignatura.id = :idAsignatura
                  AND m.periodo.id = :idPeriodo
            )
            AND (
                SELECT COUNT(mc2)
                FROM MatriculaCalificacion mc2
                WHERE mc2.matricula.estudiante.id = e.id
                  AND mc2.asignatura.id = :idAsignatura
                  AND mc2.esDefinitiva = true
                  AND mc2.nota < :notaMinima
            ) < 2
            """)
    List<EstudianteEntity> findEstudiantesDisponiblesPorAsignatura(
            @Param("idAsignatura") Long idAsignatura,
            @Param("idPeriodo") Long idPeriodo,
            @Param("notaMinima") double notaMinima);

}