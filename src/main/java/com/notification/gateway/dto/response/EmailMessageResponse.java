package com.notification.gateway.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageResponse {
    private Long id;
    private Long templateVersionId;
    private String toEmail;
    private String ccEmails;
    private String bccEmails;
    private String status;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}
