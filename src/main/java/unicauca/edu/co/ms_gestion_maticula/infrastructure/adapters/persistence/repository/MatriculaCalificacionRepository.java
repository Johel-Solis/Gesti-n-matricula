package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.EstudianteEntity;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.MatriculaCalificacion;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity.MatriculaEntity;

public interface MatriculaCalificacionRepository  extends JpaRepository<MatriculaCalificacion, Long> {

    @Query("SELECT CASE WHEN COUNT(mc) > 0 THEN true ELSE false END " +
           "FROM MatriculaCalificacion mc " +
           "WHERE mc.matricula.estudiante.id = :idEstudiante " +
           "AND mc.esDefinitiva = true " +
           "AND mc.nota >= :umbralGanado " +
           "AND mc.asignatura.idAsignatura = :idAsignatura")
    boolean asignaturaGanada(Long idEstudiante, Long idAsignatura, double umbralGanado);



    // // @Query("SELECT mc.asignatura FROM MatriculaCalificacion mc WHERE mc.matricula.estudiante.id = :idEstudiante")

    // // List<AsignaturaEntity> findAsignaturasByMatriculaEstudiante_Id(Long idEstudiante);

    //  @Query("SELECT mc.matricula.estudiante " +
    //        "FROM MatriculaCalificacion mc " +
    //        "WHERE  mc.esDefinitiva = true " +
    //        "AND mc.nota >= :umbralGanado " +
    //        "AND mc.asignatura.idAsignatura = :idAsignatura")
    // List<EstudianteEntity> getEstudiantesAsignaturaGanadasByAsignatura(Long idAsignatura, double umbralGanado);

    // // @Query("SELECT m.matricula " +
    // //        "FROM MatriculaCalificacion m " +
    // //        "WHERE m.matricula.estudiante.id NOT IN ( " +
    // //        "   SELECT mc.matricula.estudiante.id " +
    // //        "   FROM MatriculaCalificacion mc " +
    // //        "   WHERE mc.esDefinitiva = true " +
    // //        "   AND mc.nota >= :umbralGanado " +
    // //        "   AND mc.asignatura.idAsignatura = :idAsignatura " +
    // //        ") " +
    // //        "AND m.asignatura.idAsignatura = :idAsignatura")

    // // los estudiantes que no han ganado una asignatura 2 veces

    // @Query("SELECT m.matricula " +
    //        "FROM MatriculaCalificacion m " +
    //        "WHERE m.matricula.estudiante.id NOT IN ( " +
    //        "   SELECT mc.matricula.estudiante.id " +
    //        "   FROM MatriculaCalificacion mc " +
    //        "   WHERE mc.esDefinitiva = true " +
    //        "   AND mc.nota >= :umbralGanado " +
    //        "   AND mc.asignatura.idAsignatura = :idAsignatura " +
    //        ") " +
    //        "AND m.asignatura.idAsignatura = :idAsignatura")
    // List<MatriculaEntity> getEstudiantesNoAsignaturaGanadaByAsignatura(Long idAsignatura, double umbralGanado);
    
} 
