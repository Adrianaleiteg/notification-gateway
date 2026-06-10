package com.notification.gateway.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.notification.gateway.dto.request.UserRequest;
import com.notification.gateway.dto.response.UserResponse;
import com.notification.gateway.exception.ResourceNotFoundException;
import com.notification.gateway.mapper.UserMapper;
import com.notification.gateway.model.EmailMessage;
import com.notification.gateway.model.User;
import com.notification.gateway.model.enums.GroupArea;
import com.notification.gateway.provider.EmailProvider;
import com.notification.gateway.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailProvider emailProvider;

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return userMapper.toResponse(user);
    }

    public UserResponse save(UserRequest request) {
        if (!request.getEmail().endsWith("@johndeere.com") &&
                !request.getEmail().endsWith("@bancojohndeere.com")) {
            throw new ResourceNotFoundException(
                    "Only emails from @johndeere.com and @bancojohndeere.com are allowed");
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("ROLE_USER");
        }
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }

    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        EmailMessage emailMessage = EmailMessage.builder()
                .toEmail(email)
                .status(com.notification.gateway.model.enums.MessageStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        String subject = "Recuperação de senha";
        String body = "Use o token abaixo para redefinir sua senha (válido por 1 hora):\n\n" + token;
        emailProvider.send(emailMessage, subject, body, false);
    }

    public void resetPassword(String token, String newPassword) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token inválido"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new ResourceNotFoundException("Token expirado");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    public void allowGroupToCreatePublicTemplates(GroupArea group) {
        // lógica futura se quiser guardar no banco quais grupos podem criar templates
        // públicos
    }
}