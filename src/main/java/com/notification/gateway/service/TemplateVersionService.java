package com.notification.gateway.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.notification.gateway.dto.request.TemplateVersionRequest;
import com.notification.gateway.dto.response.TemplateVersionResponse;
import com.notification.gateway.exception.ResourceNotFoundException;
import com.notification.gateway.mapper.TemplateVersionMapper;
import com.notification.gateway.model.TemplateVersion;
import com.notification.gateway.repository.TemplateVersionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateVersionService {
    private final TemplateVersionRepository templateVersionRepository;
    private final TemplateVersionMapper templateVersionMapper;

    public List<TemplateVersionResponse> findAll() {
        return templateVersionRepository.findAll()
                .stream()
                .map(templateVersionMapper::toResponse)
                .toList();
    }

    public List<TemplateVersionResponse> findByTemplateId(Long templateId) {
        return templateVersionRepository.findByTemplateId(templateId)
                .stream()
                .map(templateVersionMapper::toResponse)
                .toList();
    }

    public TemplateVersionResponse findById(Long id) {
        TemplateVersion templateVersion = templateVersionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template version not found"));
        return templateVersionMapper.toResponse(templateVersion);
    }

    public TemplateVersionResponse save(TemplateVersionRequest request) {
        TemplateVersion templateVersion = templateVersionMapper.toEntity(request);

        int nextVersion = templateVersionRepository
                .findByTemplateId(request.getTemplateId()).size() + 1;
        templateVersion.setVersionNumber(nextVersion);

        TemplateVersion saved = templateVersionRepository.save(templateVersion);
        return templateVersionMapper.toResponse(saved);
    }
}
