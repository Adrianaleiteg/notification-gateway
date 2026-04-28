package com.notification.gateway.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.notification.gateway.dto.response.EmailMessageResponse;
import com.notification.gateway.model.EmailMessage;
import com.notification.gateway.model.TemplateVersion;
import com.notification.gateway.model.enums.ContentType;
import com.notification.gateway.provider.EmailProvider;
import com.notification.gateway.repository.EmailMessageRepository;
import com.notification.gateway.repository.TemplateVersionRepository;
import com.notification.gateway.service.EmailMessageService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

public class NotificationScheduler {
    private final EmailMessageService emailMessageService;
    private final EmailProvider emailProvider;
    private final TemplateVersionRepository templateVersionRepository;
    private final EmailMessageRepository emailMessageRepository;

    @Scheduled(fixedRate = 60000) // Executa a cada minuto
    public void findSchedules() {
        List<EmailMessageResponse> email = emailMessageService.findScheduled();
        for (EmailMessageResponse scheduledEmails : email) {
            if (scheduledEmails.getScheduledAt() != null
                    && scheduledEmails.getScheduledAt().isBefore(LocalDateTime.now())) {
                Long versionId = scheduledEmails.getTemplateVersionId();
                Long id = scheduledEmails.getId();
                TemplateVersion templateVersion = templateVersionRepository.findById(versionId)
                        .orElse(null);
                EmailMessage emailMessage = emailMessageRepository.findById(id)
                        .orElse(null);

                if (emailMessage != null && templateVersion != null) {
                    boolean isHtml = templateVersion.getContentType() == ContentType.HTML;
                    emailProvider.send(emailMessage, templateVersion.getSubject(), templateVersion.getBody(), isHtml);
                }
            }
        }

    }
}
