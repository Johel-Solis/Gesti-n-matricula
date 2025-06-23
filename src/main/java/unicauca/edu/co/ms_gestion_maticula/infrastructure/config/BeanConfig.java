package unicauca.edu.co.ms_gestion_maticula.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import unicauca.edu.co.ms_gestion_maticula.app.usecases.GestionarPeriodoAcademicoUseCase;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.PeriodoAcademicoRepository;

@Configuration
public class BeanConfig {

    @Bean
    public GestionarPeriodoAcademicoUseCase gestionarPeriodoAcademicoUseCase(PeriodoAcademicoRepository repo) {
        return new GestionarPeriodoAcademicoUseCase(repo);
    }
}