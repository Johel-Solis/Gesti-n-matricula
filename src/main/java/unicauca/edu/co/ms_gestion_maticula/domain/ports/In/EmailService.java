package unicauca.edu.co.ms_gestion_maticula.domain.ports.In;

import java.util.concurrent.CompletableFuture;

public interface EmailService {
    CompletableFuture<Void> sendEmail(String to, String subject, String body);
    CompletableFuture<Void> sendEmailWithAttachment(String to, String subject, String body, byte[] attachment,
            String attachmentName, String contentType);

    String buildCorreoHtml(String titulo, String contenidoHtml);
}
