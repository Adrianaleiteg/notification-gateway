package com.notification.gateway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.notification.gateway.model.EmailMessage;

@Repository
public interface EmailMessageRepository extends JpaRepository<EmailMessage, Long>{
    
}
