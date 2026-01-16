package unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.entity;

public class TutorDto {
    private DocenteEntity docente;
    private Long totalEstudiantes;

    public TutorDto(DocenteEntity docente, Long totalEstudiantes) {
        this.docente = docente;
        this.totalEstudiantes = totalEstudiantes;
    }
    public DocenteEntity getDocente() {
        return docente;
    }
    public Long getTotalEstudiantes() {
        return totalEstudiantes;
    }

}
