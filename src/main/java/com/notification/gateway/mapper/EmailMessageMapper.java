package com.notification.gateway.mapper;

import org.springframework.stereotype.Component;

import com.notification.gateway.dto.request.EmailMessageRequest;
import com.notification.gateway.dto.response.EmailMessageResponse;
import com.notification.gateway.model.EmailMessage;
import com.notification.gateway.model.TemplateVersion;

@Component
public class EmailMessageMapper {
    public EmailMessage toEntity(EmailMessageRequest request) {
        TemplateVersion templateVersion = TemplateVersion.builder()
                .id(request.getTemplateVersionId())
                .build();

        return EmailMessage.builder()
                .templateVersion(templateVersion)
                .toEmail(request.getToEmail())
                .ccEmails(request.getCcEmails())
                .bccEmails(request.getBccEmails())
                .scheduledAt(request.getScheduledAt())
                .build();
    }

    public EmailMessageResponse  toResponse(EmailMessage emailMessage){
        return EmailMessageResponse.builder()
        .id(emailMessage.getId())
        .templateVersionId(emailMessage.getTemplateVersion().getId())
        .toEmail(emailMessage.getToEmail())
        .ccEmails(emailMessage.getCcEmails())
        .bccEmails(emailMessage.getBccEmails())
        .status(emailMessage.getStatus().name())
        .scheduledAt(emailMessage.getScheduledAt())
        .sentAt(emailMessage.getSentAt())
        .createdAt(emailMessage.getCreatedAt())
        .build();
    }
}
