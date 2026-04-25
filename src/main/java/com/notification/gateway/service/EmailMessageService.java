package com.notification.gateway.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.notification.gateway.repository.EmailMessageRepository;
import com.notification.gateway.dto.request.EmailMessageRequest;
import com.notification.gateway.dto.response.EmailMessageResponse;
import com.notification.gateway.exception.ResourceNotFoundException;
import com.notification.gateway.mapper.EmailMessageMapper;
import com.notification.gateway.model.EmailMessage;
import com.notification.gateway.model.enums.MessageStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailMessageService {
    private final EmailMessageRepository emailMessageRepository;
    private final EmailMessageMapper emailMessagemapper;

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

        return emailMessagemapper.toResponse(saved);
    }

    public List<EmailMessageResponse> findScheduled() {
        return emailMessageRepository.findByStatus(MessageStatus.SCHEDULED)
                .stream()
                .map(emailMessagemapper::toResponse)
                .toList();
    }
}
