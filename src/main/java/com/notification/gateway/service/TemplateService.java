package com.notification.gateway.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.notification.gateway.dto.request.TemplateRequest;
import com.notification.gateway.dto.response.TemplateResponse;
import com.notification.gateway.exception.ResourceNotFoundException;
import com.notification.gateway.mapper.TemplateMapper;
import com.notification.gateway.model.Template;
import com.notification.gateway.repository.TemplateRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateService {
    private final TemplateRepository templateRepository;
    private final TemplateMapper templateMapper;

    public List<TemplateResponse> findAll() {
        return templateRepository.findAll()
                .stream()
                .map(templateMapper::toResponse)
                .toList();
    }

    public TemplateResponse findById(Long id) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found"));
        return templateMapper.toResponse(template);
    }

    public TemplateResponse save(TemplateRequest request) {
        Template template = templateMapper.toEntity(request);
        Template saved = templateRepository.save(template);
        return templateMapper.toResponse(saved);
    }
}
