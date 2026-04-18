package com.notification.gateway.dto.request;

import com.notification.gateway.model.enums.ContentType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateVersionRequest {
    private Long templateId;
    private String subject;
    private String body;
    private ContentType contentType;
}
