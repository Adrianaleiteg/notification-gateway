package com.notification.gateway.mapper;

import org.springframework.stereotype.Component;

import com.notification.gateway.dto.request.TemplateRequest;
import com.notification.gateway.dto.response.TemplateResponse;
import com.notification.gateway.model.Template;

@Component
public class TemplateMapper {
    public Template toEntity(TemplateRequest request) {
        return Template.builder()
                .name(request.getName())
                .description(request.getDescription())
                .groupName(request.getGroupName())
                .isPublic(request.getIsPublic() != null ? request.getIsPublic() : false)
                .active(true)
                .build();
    }

    public TemplateResponse toResponse(Template template) {
        return TemplateResponse.builder()
                .id(template.getId())
                .name(template.getName())
                .description(template.getDescription())
                .active(template.getActive())
                .isPublic(template.getIsPublic())
                .groupName(template.getGroupName())
                .createdAt(template.getCreatedAt())
                .build();
    }
}