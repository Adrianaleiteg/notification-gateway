package com.notification.gateway.service;

import java.util.List;

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
        emailMessage.setStatus(MessageStatus.PENDING);
        EmailMessage saved = emailMessageRepository.save(emailMessage);

        TemplateVersion templateVersion = templateVersionRepository.findById(request.getTemplateVersionId())
                .orElseThrow(() -> new ResourceNotFoundException("TemplateVersion not found"));

        String body = templateVersion.getBody();
        String subject = templateVersion.getSubject();

        boolean isHtml = templateVersion.getContentType() == ContentType.HTML;
        emailProvider.send(saved, subject, body, isHtml);
        saved.setStatus(MessageStatus.SENT);
        EmailMessage updated = emailMessageRepository.save(saved);
        return emailMessagemapper.toResponse(updated);
    }

    public List<EmailMessageResponse> findScheduled() {
        return emailMessageRepository.findByStatus(MessageStatus.SCHEDULED)
                .stream()
                .map(emailMessagemapper::toResponse)
                .toList();
    }
}
