package com.notification.gateway.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.notification.gateway.repository.TemplateVersionRepository;
import com.notification.gateway.model.TemplateVersion;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateVersionService {
    private final TemplateVersionRepository templateVersionRepository;

    public List<TemplateVersion> findAll() {
        return templateVersionRepository.findAll();
    }

    public TemplateVersion findById(Long id) {
        return templateVersionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template Version not found"));
    }

    public TemplateVersion save(TemplateVersion templateVersion) {
        return templateVersionRepository.save(templateVersion);
    }
}
