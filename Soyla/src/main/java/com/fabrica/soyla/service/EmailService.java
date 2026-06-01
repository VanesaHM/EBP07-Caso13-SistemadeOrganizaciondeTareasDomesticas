package com.fabrica.soyla.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final boolean mailEnabled;
    private final String fromAddress;
    private final String frontendBaseUrl;

    public EmailService(
        ObjectProvider<JavaMailSender> mailSenderProvider,
        @Value("${app.mail.enabled:false}") boolean mailEnabled,
        @Value("${app.mail.from:no-reply@soyla.local}") String fromAddress,
        @Value("${app.frontend.base-url:http://localhost:5173}") String frontendBaseUrl
    ) {
        this.mailSender = mailSenderProvider.getIfAvailable();
        this.mailEnabled = mailEnabled;
        this.fromAddress = fromAddress;
        this.frontendBaseUrl = frontendBaseUrl.replaceAll("/+$", "");
    }

    public String buildEmailConfirmationUrl(String confirmationPath) {
        if (confirmationPath == null || confirmationPath.isBlank()) {
            return frontendBaseUrl;
        }
        String normalizedPath = confirmationPath.startsWith("/") ? confirmationPath : "/" + confirmationPath;
        return frontendBaseUrl + normalizedPath;
    }

    @Async
    public void sendEmailConfirmation(String toAddress, String fullName, String confirmationPath) {
        String confirmationUrl = buildEmailConfirmationUrl(confirmationPath);

        if (!mailEnabled) {
            LOGGER.info("Email delivery is disabled. Confirmation link for {}: {}", toAddress, confirmationUrl);
            return;
        }
        if (mailSender == null) {
            LOGGER.warn("Email delivery is enabled but JavaMailSender is not configured. Confirmation link for {}: {}", toAddress, confirmationUrl);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(toAddress);
        message.setSubject("Confirma tu correo en Soyla");
        message.setText("""
            Hola %s,

            Gracias por registrarte en Soyla.

            Confirma tu correo abriendo este enlace:
            %s

            Este enlace vence en 24 horas.

            Si no creaste esta cuenta, puedes ignorar este mensaje.
            """.formatted(fullName, confirmationUrl));

        try {
            mailSender.send(message);
        } catch (MailException exception) {
            LOGGER.warn("No fue posible enviar el correo de confirmacion a {}. Confirmation link: {}", toAddress, confirmationUrl, exception);
        }
    }
}
