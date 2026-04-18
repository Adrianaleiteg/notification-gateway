package com.notification.gateway.service;

import com.notification.gateway.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import com.notification.gateway.dto.request.UserRequest;
import com.notification.gateway.dto.response.UserResponse;
import com.notification.gateway.mapper.UserMapper;
import com.notification.gateway.model.User;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponse(user);
    }

    public UserResponse findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toResponse(user);
    }

    public UserResponse save(UserRequest request) {
        User user = userMapper.toEntity(request);
        User saved = userRepository.save(user);
        return userMapper.toResponse(saved);
    }
}
