package com.notification.gateway.provider;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.notification.gateway.model.EmailMessage;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;

import lombok.extern.slf4j.Slf4j;

/**
 * Sends e-mail through the Resend HTTPS API (port 443) using the official
 * Resend Java SDK. Cloud hosts such as Railway block outbound SMTP ports,
 * so an HTTP-based provider is required in production.
 *
 * Active when {@code email.provider=resend} (env var EMAIL_PROVIDER=resend).
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "email.provider", havingValue = "resend")
public class ResendEmailProvider implements EmailProvider {

    @Value("${resend.api-key:}")
    private String apiKey;

    @Value("${resend.from:}")
    private String fromAddress;

    @Override
    public void send(EmailMessage emailMessage, String subject, String body, boolean isHtml) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Resend não configurado: defina a variável de ambiente RESEND_API_KEY");
        }
        if (fromAddress == null || fromAddress.isBlank()) {
            throw new IllegalStateException(
                    "Endereço de envio não configurado: defina a variável de ambiente MAIL_FROM");
        }

        try {
            Resend resend = new Resend(apiKey);

            CreateEmailOptions.Builder builder = CreateEmailOptions.builder()
                    .from(fromAddress)
                    .to(emailMessage.getToEmail())
                    .subject(subject);

            if (isHtml) {
                builder.html(body);
            } else {
                builder.text(body);
            }

            List<String> cc = splitEmails(emailMessage.getCcEmails());
            if (!cc.isEmpty()) {
                builder.cc(cc);
            }
            List<String> bcc = splitEmails(emailMessage.getBccEmails());
            if (!bcc.isEmpty()) {
                builder.bcc(bcc);
            }

            resend.emails().send(builder.build());
            log.info("E-mail enviado (Resend) para: {}", emailMessage.getToEmail());

        } catch (ResendException e) {
            log.error("Erro Resend ao enviar e-mail para {}: {}", emailMessage.getToEmail(), e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage());
        }
    }

    private List<String> splitEmails(String raw) {
        List<String> result = new ArrayList<>();
        if (raw != null && !raw.isBlank()) {
            for (String part : raw.split(",")) {
                if (!part.isBlank()) {
                    result.add(part.trim());
                }
            }
        }
        return result;
    }
}
