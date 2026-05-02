package com.notification.gateway.dto.request;

import com.notification.gateway.model.enums.ContentType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateVersionRequest {
    @NotNull
    private Long templateId;

    @NotBlank
    @Size(min = 2, max = 100)
    private String subject;

    @NotBlank
    private String body;

    @NotNull
    private ContentType contentType;
}
