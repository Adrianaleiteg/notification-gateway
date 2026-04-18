package com.notification.gateway.mapper;

import org.springframework.stereotype.Component;

import com.notification.gateway.dto.request.UserRequest;
import com.notification.gateway.dto.response.UserResponse;
import com.notification.gateway.model.User;

@Component
public class UserMapper {
    public User toEntity(UserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .role(request.getRole())
                .password(request.getPassword())
                .build();
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
