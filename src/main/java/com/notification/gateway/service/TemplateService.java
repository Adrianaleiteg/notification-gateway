package com.notification.gateway.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.notification.gateway.dto.request.TemplateRequest;
import com.notification.gateway.dto.response.TemplateResponse;
import com.notification.gateway.exception.ResourceNotFoundException;
import com.notification.gateway.mapper.TemplateMapper;
import com.notification.gateway.model.Template;
import com.notification.gateway.model.User;
import com.notification.gateway.repository.TemplateRepository;
import com.notification.gateway.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TemplateService {
    private final TemplateRepository templateRepository;
    private final TemplateMapper templateMapper;
    private final UserRepository userRepository;

    public List<TemplateResponse> findAll(String email) {
        return templateRepository.findByGroupName(currentGroup(email))
                .stream()
                .map(templateMapper::toResponse)
                .toList();
    }

    public TemplateResponse findById(Long id) {
        Template template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found"));
        return templateMapper.toResponse(template);
    }

    public TemplateResponse save(TemplateRequest request, String email) {
        Template template = templateMapper.toEntity(request);
        template.setGroupName(currentGroup(email));
        Template saved = templateRepository.save(template);
        return templateMapper.toResponse(saved);
    }

    // Resolves the group of the authenticated user (identified by email).
    private String currentGroup(String email) {
        return userRepository.findByEmail(email)
                .map(User::getGroupName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
