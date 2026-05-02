package com.notification.gateway.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.notification.gateway.dto.request.TemplateVersionRequest;
import com.notification.gateway.dto.response.TemplateVersionResponse;
import com.notification.gateway.service.TemplateVersionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/templates/{templateId}/versions")
@RequiredArgsConstructor
public class TemplateVersionController {
    private final TemplateVersionService templateVersionService;

    @GetMapping
    public ResponseEntity<List<TemplateVersionResponse>> findAll(@PathVariable Long templateId) {
        return ResponseEntity.ok(templateVersionService.findByTemplateId(templateId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateVersionResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(templateVersionService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TemplateVersionResponse> save(@Valid @RequestBody TemplateVersionRequest templateVersion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateVersionService.save(templateVersion));
    }
}