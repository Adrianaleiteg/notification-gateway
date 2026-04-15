package com.notification.gateway.model;

import java.time.LocalDateTime;

import com.notification.gateway.model.enums.MessageStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "email_messages")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class EmailMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "template_version_id", nullable = false)
private TemplateVersion templateVersion;

    @Column(nullable = false)
    private String toEmail;

    @Column(nullable = false)
    private String ccEmails;

    @Column
    private String bccEmails;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status;

    @Column
    private LocalDateTime scheduledAt ;

    @Column
    private LocalDateTime sentAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

     @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
