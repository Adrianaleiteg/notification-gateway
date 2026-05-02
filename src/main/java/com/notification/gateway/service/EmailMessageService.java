package com.notification.gateway.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.notification.gateway.dto.request.EmailMessageRequest;
import com.notification.gateway.dto.response.EmailMessageResponse;
import com.notification.gateway.exception.ResourceNotFoundException;
import com.notification.gateway.mapper.EmailMessageMapper;
import com.notification.gateway.model.EmailMessage;
import com.notification.gateway.model.TemplateVersion;
import com.notification.gateway.model.enums.ContentType;
import com.notification.gateway.model.enums.MessageStatus;
import com.notification.gateway.provider.EmailProvider;
import com.notification.gateway.repository.EmailMessageRepository;
import com.notification.gateway.repository.TemplateVersionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailMessageService {
    private final EmailMessageRepository emailMessageRepository;
    private final EmailMessageMapper emailMessagemapper;
    private final EmailProvider emailProvider;
    private final TemplateVersionRepository templateVersionRepository;

    public List<EmailMessageResponse> findAll() {
        return emailMessageRepository.findAll()
                .stream()
                .map(emailMessagemapper::toResponse)
                .toList();
    }

    public EmailMessageResponse findById(Long id) {
        EmailMessage emailMessage = emailMessageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Email message not found"));
        return emailMessagemapper.toResponse(emailMessage);
    }

    public EmailMessageResponse save(EmailMessageRequest request) {
        EmailMessage emailMessage = emailMessagemapper.toEntity(request);

        if (emailMessage.getScheduledAt() != null &&
                emailMessage.getScheduledAt().isAfter(LocalDateTime.now())) {

            emailMessage.setStatus(MessageStatus.SCHEDULED);
            EmailMessage saved = emailMessageRepository.save(emailMessage);
            return emailMessagemapper.toResponse(saved);

        } else {
            emailMessage.setStatus(MessageStatus.PENDING);
            EmailMessage saved = emailMessageRepository.save(emailMessage);

            TemplateVersion templateVersion = templateVersionRepository.findById(request.getTemplateVersionId())
                    .orElseThrow(() -> new ResourceNotFoundException("TemplateVersion not found"));

            String body = templateVersion.getBody();
            if (request.getVariables() != null && !request.getVariables().isEmpty()) {
                body = replaceVariables(body, request.getVariables());
            }

            boolean isHtml = templateVersion.getContentType() == ContentType.HTML;
            emailProvider.send(saved, templateVersion.getSubject(), body, isHtml);

            saved.setStatus(MessageStatus.SENT);
            EmailMessage updated = emailMessageRepository.save(saved);
            return emailMessagemapper.toResponse(updated);
        }

    }

    public List<EmailMessageResponse> findScheduled() {
        return emailMessageRepository.findByStatus(MessageStatus.SCHEDULED)
                .stream()
                .map(emailMessagemapper::toResponse)
                .toList();
    }

    private String replaceVariables(String body, Map<String, String> variables) {
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String chave = entry.getKey();
            String valor = entry.getValue();

            body = body.replace("{{" + chave + "}}", valor);

        }
        return body;
    }
}
