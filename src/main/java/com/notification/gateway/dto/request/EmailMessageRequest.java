package com.notification.gateway.dto.request;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageRequest {
    private Long templateVersionId;
    private String toEmail;
    private String ccEmails;
    private String bccEmails;
    private LocalDateTime scheduledAt;
}
