package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity;


public class MatriculaCursoDto {
     private CursoEntity curso;
    private Long totalMatriculas;

    public MatriculaCursoDto(CursoEntity curso, Long totalMatriculas) {
        this.curso = curso;
        this.totalMatriculas = totalMatriculas;
    }

    public CursoEntity getCurso() {
        return curso;
    }

    public Long getTotalMatriculas() {
        return totalMatriculas;
    }


    
}
