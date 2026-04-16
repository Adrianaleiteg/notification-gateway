package com.notification.gateway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.notification.gateway.model.EmailMessage;
import com.notification.gateway.model.enums.MessageStatus;

@Repository
public interface EmailMessageRepository extends JpaRepository<EmailMessage, Long> {
    List<EmailMessage> findByStatus(MessageStatus status);

}
