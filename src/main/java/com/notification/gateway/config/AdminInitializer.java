package com.notification.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.notification.gateway.model.User;
import com.notification.gateway.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Seeds an initial admin user so the system isn't locked out once user
 * creation is restricted to admins. Runs only when ADMIN_EMAIL and
 * ADMIN_PASSWORD are configured, and only if no admin exists yet.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email:}")
    private String adminEmail;

    @Value("${admin.password:}")
    private String adminPassword;

    @Value("${admin.name:Admin}")
    private String adminName;

    @Bean
    CommandLineRunner seedAdmin() {
        return args -> {
            if (adminEmail.isBlank() || adminPassword.isBlank()) {
                return;
            }

            boolean adminExists = userRepository.findAll().stream()
                    .anyMatch(u -> "ROLE_ADMIN".equals(u.getRole()));
            if (adminExists || userRepository.findByEmail(adminEmail).isPresent()) {
                return;
            }

            User admin = User.builder()
                    .name(adminName)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role("ROLE_ADMIN")
                    .build();
            userRepository.save(admin);
            log.info("Usuário admin inicial criado: {}", adminEmail);
        };
    }
}
