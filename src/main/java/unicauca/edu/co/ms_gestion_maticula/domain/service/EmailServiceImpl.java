package unicauca.edu.co.ms_gestion_maticula.domain.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
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
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            if (mailFrom != null && !mailFrom.isBlank()) {
                helper.setFrom(mailFrom);
            }
            helper.setSubject(subject);
            helper.setText(body, false);
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
}
