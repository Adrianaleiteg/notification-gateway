package com.notification.gateway.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.notification.gateway.model.EmailMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * Sends e-mail through the Resend HTTPS API (port 443) instead of raw SMTP.
 * Cloud hosts such as Railway block outbound SMTP ports, so an HTTP-based
 * provider is required in production.
 *
 * Active when {@code email.provider=resend} (env var EMAIL_PROVIDER=resend).
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "email.provider", havingValue = "resend")
public class ResendEmailProvider implements EmailProvider {

    private static final String RESEND_URL = "https://api.resend.com/emails";

    @Value("${resend.api-key:}")
    private String apiKey;

    @Value("${resend.from:}")
    private String fromAddress;

    private final RestClient restClient = RestClient.create();

    @Override
    public void send(EmailMessage emailMessage, String subject, String body, boolean isHtml) {
        try {
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalStateException(
                        "Resend não configurado: defina a variável de ambiente RESEND_API_KEY");
            }
            if (fromAddress == null || fromAddress.isBlank()) {
                throw new IllegalStateException(
                        "Endereço de envio não configurado: defina a variável de ambiente MAIL_FROM");
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("from", fromAddress);
            payload.put("to", List.of(emailMessage.getToEmail()));
            payload.put("subject", subject);
            payload.put(isHtml ? "html" : "text", body);

            List<String> cc = splitEmails(emailMessage.getCcEmails());
            if (!cc.isEmpty()) {
                payload.put("cc", cc);
            }
            List<String> bcc = splitEmails(emailMessage.getBccEmails());
            if (!bcc.isEmpty()) {
                payload.put("bcc", bcc);
            }

            restClient.post()
                    .uri(RESEND_URL)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("E-mail enviado (Resend) para: {}", emailMessage.getToEmail());

        } catch (RestClientResponseException e) {
            // Resend returns a JSON body explaining the failure (e.g. domain not verified).
            String detail = e.getResponseBodyAsString();
            log.error("Erro Resend ao enviar e-mail para {}: {} {}",
                    emailMessage.getToEmail(), e.getStatusCode(), detail);
            throw new RuntimeException("Falha ao enviar e-mail: " + detail);
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail para {}: {}", emailMessage.getToEmail(), e.getMessage());
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
