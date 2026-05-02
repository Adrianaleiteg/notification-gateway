package com.notification.gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.notification.gateway.dto.request.EmailMessageRequest;
import com.notification.gateway.dto.response.EmailMessageResponse;
import com.notification.gateway.service.EmailMessageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/email-messages")
@RequiredArgsConstructor
public class EmailMessageController {
    private final EmailMessageService emailMessageService;

    @GetMapping
    public ResponseEntity<List<EmailMessageResponse>> findAll() {
        return ResponseEntity.ok(emailMessageService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailMessageResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(emailMessageService.findById(id));
    }

    @PostMapping
    public ResponseEntity<EmailMessageResponse> save(@Valid @RequestBody EmailMessageRequest emailMessage) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emailMessageService.save(emailMessage));
    }

}
