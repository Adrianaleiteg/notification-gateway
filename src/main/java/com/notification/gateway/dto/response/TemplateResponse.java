package com.notification.gateway.dto.response;

import java.time.LocalDateTime;

import com.notification.gateway.model.enums.GroupArea;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private Boolean isPublic;
    private GroupArea groupName;
    private LocalDateTime createdAt;
}