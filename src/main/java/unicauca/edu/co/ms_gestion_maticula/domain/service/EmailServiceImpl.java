package unicauca.edu.co.ms_gestion_maticula.domain.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.internet.MimeMessage;

import lombok.RequiredArgsConstructor;
import unicauca.edu.co.ms_gestion_maticula.domain.ports.In.EmailService;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    @Override
    @Async
    public CompletableFuture<Void> sendEmail(String to, String subject, String body) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("El destinatario es requerido");
        }
        if (subject == null) {
            subject = "";
        }
        if (body == null) {
            body = "";
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            if (mailFrom != null && !mailFrom.isBlank()) {
                message.setFrom(mailFrom);
            }
            message.setSubject(subject);
            message.setText(body);
            LOGGER.info("Enviando correo a {}", to);
            mailSender.send(message);
            LOGGER.info("Correo enviado a {}", to);
            return CompletableFuture.completedFuture(null);
        } catch (Exception ex) {
            LOGGER.error("Error enviando correo a {}", to, ex);
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(ex);
            return failed;
        }
    }

    @Override
    @Async
    public CompletableFuture<Void> sendEmailWithAttachment(String to, String subject, String body, byte[] attachment,
            String attachmentName, String contentType) {
        if (to == null || to.isBlank()) {
            throw new IllegalArgumentException("El destinatario es requerido");
        }
        if (subject == null) {
            subject = "";
        }
        if (body == null) {
            body = "";
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    "UTF-8");
            helper.setTo(to);
            if (mailFrom != null && !mailFrom.isBlank()) {
                helper.setFrom(mailFrom);
            }

            helper.setSubject(subject);
            helper.setText(body, true);
            ClassPathResource logo = new ClassPathResource("image/escudo-unicauca.png");
            helper.addInline("logo-unicauca", logo, "image/png");
            if (attachment != null && attachment.length > 0 && attachmentName != null && !attachmentName.isBlank()) {
                String resolvedType = (contentType == null || contentType.isBlank())
                        ? "application/octet-stream"
                        : contentType;
                helper.addAttachment(attachmentName, new ByteArrayResource(attachment), resolvedType);
            }
            LOGGER.info("Enviando correo con adjunto a {}", to);
            mailSender.send(message);
            LOGGER.info("Correo enviado a {}", to);
            return CompletableFuture.completedFuture(null);
        } catch (Exception ex) {
            LOGGER.error("Error enviando correo con adjunto a {}", to, ex);
            CompletableFuture<Void> failed = new CompletableFuture<>();
            failed.completeExceptionally(ex);
            return failed;
        }
    }


     public String buildCorreoHtml(String titulo, String contenidoHtml) {
        String cidLogo = "logo-unicauca";
        return "<!DOCTYPE html>"
                + "<html lang=\"es\">"
                + "<head>"
                + "<meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                + "<title>Correo Institucional</title>"
                + "</head>"
                + "<body style=\"margin: 0; padding: 0; background-color: #f4f4f4; font-family: Arial, sans-serif;\">"
                + "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" "
                + "style=\"background-color: #1a4480; color: #ffffff; border-collapse: collapse;\">"
                + "<tr>"
                + "<td align=\"center\" style=\"padding: 40px 20px 20px 20px;\">"
                + "<img src=\"cid:" + cidLogo + "\" "
                + "alt=\"Universidad del Cauca\" width=\"120\" style=\"display: block; margin-bottom: 15px;\" >"
                + "<h2 style=\"margin: 0; font-size: 22px; font-weight: normal;\">Universidad del Cauca</h2>"
                + "<div style=\"width: 80%; border-bottom: 1px solid #ffffff; margin: 10px 0;\"></div>"
                + "<h3 style=\"margin: 5px 0; font-size: 14px; opacity: 0.9;\">"
                + "Maestría en Computación"
                + "</h3>"
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td align=\"center\" style=\"padding: 20px;\">"
                + "<h1 style=\"font-size: 28px; margin: 0;\">" + titulo + "</h1>"
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style=\"padding: 20px 40px; text-align: left; font-size: 16px; line-height: 1.5;\">"
                + contenidoHtml
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td style=\"padding: 40px 40px 60px 40px; font-size: 12px; line-height: 1.4; opacity: 0.8; "
                + "position: relative;\">"
                + "<p><i>Por favor no responda a este correo electronico.</i></p>"
                + "</td>"
                + "</tr>"
                + "<tr>"
                + "<td align=\"right\" style=\"padding: 0; line-height: 0;\">"
                + "<div style=\"width: 0; height: 0; border-style: solid; border-width: 0 0 80px 80px; "
                + "border-color: transparent transparent #e30613 transparent;\"></div>"
                + "</td>"
                + "</tr>"
                + "</table>"
                + "</body>"
                + "</html>";
    }
}
