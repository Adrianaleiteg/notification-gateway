package com.notification.gateway.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequest {
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    private String description;
}
