package com.notification.gateway.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.notification.gateway.model.EmailMessage;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmtpEmailProvider implements EmailProvider {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Override
    public void send(EmailMessage emailMessage, String subject, String body, boolean isHtml) {
        try {
            if (fromAddress == null || fromAddress.isBlank()) {
                throw new IllegalStateException(
                        "Endereço de envio não configurado: defina a variável de ambiente MAIL_USERNAME");
            }

            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");

            helper.setFrom(fromAddress);
            helper.setTo(emailMessage.getToEmail());
            helper.setSubject(subject);
            helper.setText(body, isHtml);

            if (emailMessage.getCcEmails() != null && !emailMessage.getCcEmails().isBlank()) {
                helper.setCc(emailMessage.getCcEmails().split(","));
            }

            if (emailMessage.getBccEmails() != null && !emailMessage.getBccEmails().isBlank()) {
                helper.setBcc(emailMessage.getBccEmails().split(","));
            }

            mailSender.send(mime);
            log.info("E-mail enviado para: {}", emailMessage.getToEmail());

        } catch (Exception e) {
            log.error("Erro ao enviar e-mail para {}: {}", emailMessage.getToEmail(), e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage());
        }
    }

    private boolean isHtml(EmailMessage emailMessage) {
        return emailMessage.getTemplateVersion().getContentType().name().equals("HTML");
    }
}