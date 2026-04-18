package com.notification.gateway.dto.response;

import java.time.LocalDateTime;

import com.notification.gateway.model.enums.ContentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateVersionResponse {
    private Long id;
    private Long templateId;
    private Integer versionNumber;
    private String subject;
    private String body;
    private ContentType contentType;
    private LocalDateTime createdAt;
}
