package com.notification.gateway.mapper;

import org.springframework.stereotype.Component;

import com.notification.gateway.dto.request.TemplateVersionRequest;
import com.notification.gateway.dto.response.TemplateVersionResponse;
import com.notification.gateway.model.Template;
import com.notification.gateway.model.TemplateVersion;

@Component
public class TemplateVersionMapper {
    public TemplateVersion toEntity(TemplateVersionRequest request) {
    Template template = Template.builder()
            .id(request.getTemplateId())
            .build();

    return TemplateVersion.builder()
            .template(template)
            .subject(request.getSubject())
            .body(request.getBody())
            .contentType(request.getContentType())
            .build();
}

public TemplateVersionResponse toResponse(TemplateVersion templateVersion) {
    return TemplateVersionResponse.builder()
            .id(templateVersion.getId())
            .templateId(templateVersion.getTemplate().getId())
            .versionNumber(templateVersion.getVersionNumber())
            .subject(templateVersion.getSubject())
            .body(templateVersion.getBody())
            .contentType(templateVersion.getContentType())
            .createdAt(templateVersion.getCreatedAt())
            .build();
}
}
