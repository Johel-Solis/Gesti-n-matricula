package unicauca.edu.co.ms_gestion_maticula.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI msGestionMatriculaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("ms_gestion_maticula API")
                        .description("API del microservicio de gestion de matricula")
                        .version("v1"));
    }
}
