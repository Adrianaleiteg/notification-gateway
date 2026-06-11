package com.notification.gateway.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.notification.gateway.model.EmailMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * Sends e-mail through the Brevo (ex-Sendinblue) HTTPS API (port 443).
 * Cloud hosts such as Railway block outbound SMTP ports, so an HTTP-based
 * provider is required in production.
 *
 * Brevo allows a single verified sender e-mail (e.g. a Gmail address) without
 * owning a domain, which is why it is used here.
 *
 * Active when {@code email.provider=brevo} (env var EMAIL_PROVIDER=brevo).
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "email.provider", havingValue = "brevo")
public class BrevoEmailProvider implements EmailProvider {

    private static final String BREVO_URL = "https://api.brevo.com/v3/smtp/email";

    @Value("${brevo.api-key:}")
    private String apiKey;

    @Value("${brevo.from:}")
    private String fromAddress;

    @Value("${brevo.sender-name:Hermes}")
    private String senderName;

    private final RestClient restClient = RestClient.create();

    @Override
    public void send(EmailMessage emailMessage, String subject, String body, boolean isHtml) {
        try {
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalStateException(
                        "Brevo não configurado: defina a variável de ambiente BREVO_API_KEY");
            }
            if (fromAddress == null || fromAddress.isBlank()) {
                throw new IllegalStateException(
                        "Endereço de envio não configurado: defina a variável de ambiente MAIL_FROM");
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("sender", sender());
            payload.put("to", recipients(List.of(emailMessage.getToEmail())));
            payload.put("subject", subject);
            payload.put(isHtml ? "htmlContent" : "textContent", body);

            List<String> cc = splitEmails(emailMessage.getCcEmails());
            if (!cc.isEmpty()) {
                payload.put("cc", recipients(cc));
            }
            List<String> bcc = splitEmails(emailMessage.getBccEmails());
            if (!bcc.isEmpty()) {
                payload.put("bcc", recipients(bcc));
            }

            restClient.post()
                    .uri(BREVO_URL)
                    .header("api-key", apiKey)
                    .header("accept", MediaType.APPLICATION_JSON_VALUE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();

            log.info("E-mail enviado (Brevo) para: {}", emailMessage.getToEmail());

        } catch (RestClientResponseException e) {
            // Brevo returns a JSON body explaining the failure (e.g. unverified sender).
            String detail = e.getResponseBodyAsString();
            log.error("Erro Brevo ao enviar e-mail para {}: {} {}",
                    emailMessage.getToEmail(), e.getStatusCode(), detail);
            throw new RuntimeException("Falha ao enviar e-mail: " + detail);
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail para {}: {}", emailMessage.getToEmail(), e.getMessage());
            throw new RuntimeException("Falha ao enviar e-mail: " + e.getMessage());
        }
    }

    private Map<String, String> sender() {
        Map<String, String> sender = new HashMap<>();
        // Accepts either "email@x.com" or "Name <email@x.com>".
        String name = senderName;
        String email = fromAddress.trim();
        int lt = email.indexOf('<');
        int gt = email.indexOf('>');
        if (lt >= 0 && gt > lt) {
            String parsedName = email.substring(0, lt).trim();
            if (!parsedName.isBlank()) {
                name = parsedName;
            }
            email = email.substring(lt + 1, gt).trim();
        }
        sender.put("email", email);
        if (name != null && !name.isBlank()) {
            sender.put("name", name);
        }
        return sender;
    }

    private List<Map<String, String>> recipients(List<String> emails) {
        List<Map<String, String>> list = new ArrayList<>();
        for (String email : emails) {
            list.add(Map.of("email", email));
        }
        return list;
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
