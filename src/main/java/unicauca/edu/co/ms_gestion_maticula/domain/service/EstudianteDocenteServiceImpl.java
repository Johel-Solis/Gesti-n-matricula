package unicauca.edu.co.ms_gestion_maticula.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.enums.MatriculaEstado;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Docente;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Estudiante;
import unicauca.edu.co.ms_gestion_maticula.domain.model.Matricula;
import unicauca.edu.co.ms_gestion_maticula.domain.model.PeriodoAcademico;
import unicauca.edu.co.ms_gestion_maticula.domain.model.TutorEstudiante;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.In.EstudianteDocenteService;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.In.PeriodoAcademicoService;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.In.EmailService;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.out.EstudianteDocenteRepository;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.out.MatriculaRepository;
import unicauca.edu.co.ms_gestion_maticula.domain.response.EstudianteResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.EstudianteTutorResponse;
import unicauca.edu.co.ms_gestion_maticula.domain.response.TutorNotificacionResponse;
import unicauca.edu.co.ms_gestion_maticula.infrastructure.adapters.persistence.PeriodoAcademicoJpaAdapter;


@Service
@RequiredArgsConstructor
public class EstudianteDocenteServiceImpl implements EstudianteDocenteService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EstudianteDocenteServiceImpl.class);

    @Autowired
    private final EstudianteDocenteRepository estudianteDocenteRepo;

    @Autowired
    private final ModelMapper modelMapper;

    @Autowired
    private final MatriculaRepository matriculaRepository;

    @Autowired
    private final EmailService emailService;
    
    @Autowired
    private final PeriodoAcademicoJpaAdapter periodoRepository;

    @Override
    public List<TutorEstudiante> getDirectores() {
        return estudianteDocenteRepo.getDirectores();
                
        
    }

    @Override
    public List<EstudianteTutorResponse> getEstudiantesByTutor(Long tutorId) {
        return estudianteDocenteRepo.findEstudiantesByTutor(tutorId).stream()
                .map(this::toEstudianteTutorResponse)
                .toList();
        
    }

    @Override
    public List<EstudianteResponse> getEstudiantesMatriculados() {

        PeriodoAcademico periodoActivo = periodoRepository.findPeriodoActivo()
                .orElseThrow(() -> new RuntimeException("No hay un periodo academico activo"));
        return estudianteDocenteRepo.findEstudiantesMatriculados(periodoActivo.getId(), MatriculaEstado.APROBADA.name())
                .stream()
                .map(estudiante -> modelMapper.map(estudiante, EstudianteResponse.class))
                .toList();


    }
    private int countMatriculasPendientes(Long estudianteId) {
        List<Matricula> matriculas = matriculaRepository.findByEstudianteIdAndPeriodoActivo(estudianteId);
        int pendientes = 0;
        for (Matricula matricula : matriculas) {
            if (matricula != null && matricula.getEstadoMatricula() != null &&
                    MatriculaEstado.CREADA.name().equalsIgnoreCase(matricula.getEstadoMatricula())) {
                pendientes++;
            }
        }
        return pendientes;
    }

    private int countTotalMatriculas(Long estudianteId) {
        List<Matricula> matriculas = matriculaRepository.findByEstudianteIdAndPeriodoActivo(estudianteId);
        int total = matriculas.stream()
                .filter(matricula -> matricula != null && matricula.isEstado())
                .toList()
                .size();
        return total;
    }
    @Override
    public List<TutorNotificacionResponse> notificarTutoresConMatriculasActivas() {
        List<TutorEstudiante> tutores = estudianteDocenteRepo.getDirectores();
        List<TutorNotificacionResponse> resultado = new ArrayList<>();
        LOGGER.info("Notificacion prematricula: tutores encontrados {}", tutores.size());

        for (TutorEstudiante tutor : tutores) {
            Docente docente = tutor.getDocente();
            if (docente == null || docente.getId() == null) {
                LOGGER.warn("Tutor sin docente o id, se omite");
                continue;
            }
            String correo = docente.getPersona() != null ? docente.getPersona().getCorreoElectronico() : null;
            if (correo == null || correo.isBlank()) {
                LOGGER.warn("Tutor {} sin correo, se omite", docente.getId());
                continue;
            }
            List<Estudiante> estudiantes = estudianteDocenteRepo.findEstudiantesByTutor(docente.getId());
            List<Estudiante> conMatriculasActivas = estudiantes.stream()
                    .filter(estudiante -> estudiante != null && estudiante.getId() != null)
                    .filter(estudiante -> tieneMatriculaActiva(estudiante.getId()))
                    .toList();
            if (conMatriculasActivas.isEmpty()) {
                LOGGER.info("Tutor {} sin estudiantes con matricula activa", docente.getId());
                continue;
            }

            String asunto = "Revision de prematricula";
            String cuerpo = buildCuerpoCorreo(docente, conMatriculasActivas);
            LOGGER.info("Enviando correo a tutor {} ({}) con {} estudiantes",
                    docente.getId(), correo, conMatriculasActivas.size());
            emailService.sendEmail(correo, asunto, cuerpo);
            LOGGER.info("Correo enviado a tutor {}", docente.getId());

            resultado.add(TutorNotificacionResponse.builder()
                    .tutorId(docente.getId())
                    .nombre(docente.getPersona() != null ? (docente.getPersona().getNombre() + " " + docente.getPersona().getApellido()).trim() : "")
                    .codigo(docente.getCodigo())
                    .correo(correo)
                    .totalEstudiantesConMatriculaActiva(conMatriculasActivas.size())
                    .build());
        }

        LOGGER.info("Notificacion prematricula finalizada. Correos enviados {}", resultado.size());
        return resultado;
    }

    private boolean tieneMatriculaActiva(Long estudianteId) {
        List<Matricula> matriculas = matriculaRepository.findByEstudianteIdAndPeriodoActivo(estudianteId);
        return matriculas.stream().anyMatch(this::esMatriculaActiva);
    }

    private boolean esMatriculaActiva(Matricula matricula) {
        if (matricula == null) {
            return false;
        }
        if (matricula.getEstadoMatricula() != null) {
            return MatriculaEstado.CREADA.name().equalsIgnoreCase(matricula.getEstadoMatricula());
        }
        return matricula.isEstado();
    }

    private String buildCuerpoCorreo(Docente docente, List<Estudiante> estudiantes) {
        String saludo = "Hola";
        if (docente.getPersona() != null) {
            String nombre = docente.getPersona().getNombre() != null ? docente.getPersona().getNombre() : "";
            String apellido = docente.getPersona().getApellido() != null ? docente.getPersona().getApellido() : "";
            String full = (nombre + " " + apellido).trim();
            if (!full.isEmpty()) {
                saludo = "Hola " + full;
            }
        }
        String listado = estudiantes.stream()
                .map(this::formatEstudiante)
                .collect(Collectors.joining("\n- ", "\n- ", ""));
        return saludo + ",\n\n" +
                "Tienes estudiantes con prematricula activa. Por favor revisa las solicitudes:\n" +
                listado +
                "\n\nGracias.";
    }

    private String formatEstudiante(Estudiante estudiante) {
        if (estudiante == null) {
            return "";
        }
        String nombre = "";
        String apellido = "";
        if (estudiante.getPersona() != null) {
            nombre = estudiante.getPersona().getNombre() != null ? estudiante.getPersona().getNombre() : "";
            apellido = estudiante.getPersona().getApellido() != null ? estudiante.getPersona().getApellido() : "";
        }
        String full = (nombre + " " + apellido).trim();
        String codigo = estudiante.getCodigo() != null ? estudiante.getCodigo() : "";
        if (!codigo.isEmpty()) {
            return full.isEmpty() ? codigo : full + " (" + codigo + ")";
        }
        return full.isEmpty() ? "Estudiante" : full;
    }

    private EstudianteTutorResponse toEstudianteTutorResponse(Estudiante estudiante) {
        return EstudianteTutorResponse.builder()
                .estudiante( modelMapper.map(estudiante, EstudianteResponse.class))
                .totalMatriculasPendientes(countMatriculasPendientes(estudiante.getId()))
                .totalMatriculas(countTotalMatriculas(estudiante.getId()))
                .build();
    }

    
}
