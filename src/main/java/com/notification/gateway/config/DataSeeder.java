package com.notification.gateway.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.notification.gateway.model.User;
import com.notification.gateway.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("teste@admin.com").isEmpty()) {
            User admin = new User();
            admin.setName("admin");
            admin.setPassword(passwordEncoder.encode("teste123"));
            admin.setEmail("teste@admin.com");
            admin.setRole("ROLE_ADMIN");

            userRepository.save(admin);
        }

    }

}
