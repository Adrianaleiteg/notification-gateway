package com.notification.gateway.dto.request;

import java.time.LocalDateTime;
import java.util.Map;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessageRequest {
    @NotNull
    private Long templateVersionId;

    @Email
    @NotBlank
    private String toEmail;

    @Email
    private String ccEmails;

    @Email
    private String bccEmails;

    private LocalDateTime scheduledAt;

    private Map<String, String> variables;
}
