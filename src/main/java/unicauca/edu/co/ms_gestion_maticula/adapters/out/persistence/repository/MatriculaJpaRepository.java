package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.AsignaturaEntity;
import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.MatriculaEntity;

@Repository
public interface MatriculaJpaRepository extends JpaRepository<MatriculaEntity, Long> {

    List<MatriculaEntity> findByEstudianteId(Long estudianteId);



    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END " +
           "FROM MatriculaEntity m " +
           "WHERE m.estudianteId = :estudianteId " +
           "AND m.estado = :estado " +
           "AND m.periodo.id = :periodoId " +
           "AND m.curso.asignatura.id = :asignaturaId")
    boolean existsMatriculaByEstudianteIdAndPeriodoIdAndAsignaturaId(Long estudianteId, Long periodoId, Long asignaturaId, boolean estado);


    @Query("SELECT m FROM MatriculaEntity m WHERE m.estudianteId = :estudianteId AND m.curso.asignatura.id = :asignaturaId AND m.estado = :estado")
    List<MatriculaEntity> findByEstudianteIdAndCursoAsignaturaId(Long estudianteId, Long asignaturaId, boolean estado);


    @Query("SELECT m.curso.asignatura FROM MatriculaEntity m WHERE m.estudianteId = :idEstudiante AND m.periodo.id = :idPeriodo AND m.estado = :estado")
    List<AsignaturaEntity> findAsignaturasByEstudianteIdAndPeriodoId(Long idEstudiante, Long idPeriodo, boolean estado);


}
