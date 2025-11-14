package unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import unicauca.edu.co.ms_gestion_maticula.adapters.out.persistence.entity.MatriculaCalificacion;

public interface MatriculaCalificacionRepository  extends JpaRepository<MatriculaCalificacion, Long> {

    @Query("SELECT CASE WHEN COUNT(mc) > 0 THEN true ELSE false END " +
           "FROM MatriculaCalificacion mc " +
           "WHERE mc.matricula.estudianteId = :idEstudiante " +
           "AND mc.esDefinitiva = true " +
           "AND mc.nota >= 3.5 " +
           "AND mc.asignatura.idAsignatura = :idAsignatura")
    boolean asignaturaGanada(Long idEstudiante, Long idAsignatura);


    // @Query("SELECT mc.asignatura FROM MatriculaCalificacion mc WHERE mc.matricula.estudiante.id = :idEstudiante")

    // List<AsignaturaEntity> findAsignaturasByMatriculaEstudiante_Id(Long idEstudiante);
    
} 
