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

import com.notification.gateway.dto.request.TemplateRequest;
import com.notification.gateway.dto.response.TemplateResponse;
import com.notification.gateway.service.TemplateService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {
    private final TemplateService templateService;

    @GetMapping
    public ResponseEntity<List<TemplateResponse>> findAll() {
        return ResponseEntity.ok(templateService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(templateService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TemplateResponse> save(@Valid @RequestBody TemplateRequest template) {
        return ResponseEntity.status(HttpStatus.CREATED).body(templateService.save(template));
    }
}
