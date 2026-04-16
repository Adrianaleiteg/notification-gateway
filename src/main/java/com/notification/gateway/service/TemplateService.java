package com.notification.gateway.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.notification.gateway.model.Template;
import com.notification.gateway.repository.TemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateService {
    private final TemplateRepository templateRepository;

    public List<Template> findAll() {
        return templateRepository.findAll();
    }
    
    public Template findById(Long id) {
        return templateRepository.findById(id).orElseThrow(() -> new RuntimeException("Template not found"));
    }

    public Template save(Template template) {
        return templateRepository.save(template);
    }
}
