package com.notification.gateway.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.notification.gateway.repository.EmailMessageRepository;
import com.notification.gateway.model.EmailMessage;
import com.notification.gateway.model.enums.MessageStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailMessageService {
    private final EmailMessageRepository emailMessageRepository;
    
    public List<EmailMessage> findAll(){
        return emailMessageRepository.findAll();
    }

    public EmailMessage findById(Long id){
        return emailMessageRepository.findById(id).orElseThrow(() -> new RuntimeException("Email message not found"));
    }

    public EmailMessage save(EmailMessage emailMessage){
        emailMessage.setStatus(MessageStatus.PENDING);
        return emailMessageRepository.save(emailMessage);
    }

    public List<EmailMessage> findScheduled(){
        return emailMessageRepository.findByStatus(MessageStatus.SCHEDULED);
    }
}
