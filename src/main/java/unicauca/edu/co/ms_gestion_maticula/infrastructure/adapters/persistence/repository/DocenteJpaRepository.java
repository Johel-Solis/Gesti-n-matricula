package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.DocenteEntity;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.TutorDto;

public interface DocenteJpaRepository extends JpaRepository<DocenteEntity, Long> {
    Optional<DocenteEntity> findByCodigo(String codigo);
    List<DocenteEntity> findByFacultad(String facultad);

    @Query("SELECT d FROM DocenteEntity d JOIN DocenteAsignaturaEntity c ON d.id = c.docente.id WHERE c.asignatura.id = :asignaturaId")
    List<DocenteEntity> findByAsignaturaId(Long asignaturaId);

    @Query("""
        SELECT new  unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.TutorDto(de.docente, COUNT(de.id))
        FROM DocenteEstudiante de 
        WHERE de.tipo = 'Director'
        group by de.docente
    """)
    List<TutorDto> getDirectores();

    @Query("""
        SELECT de.docente
        FROM DocenteEstudiante de
        WHERE de.estudiante.id = :estudianteId
          AND de.tipo = 'Director'
    """)
    List<DocenteEntity> findTutoresByEstudiante(@Param("estudianteId") Long estudianteId);

    @Query("SELECT d FROM DocenteEntity d WHERE d.persona.correoElectronico = :email")
    Optional<DocenteEntity> findByEmail(@Param("email") String email);

    @Query("SELECT d FROM DocenteEntity d WHERE d.estado = 'ACTIVO'")
    List<DocenteEntity> getDocentesActivos();
}
