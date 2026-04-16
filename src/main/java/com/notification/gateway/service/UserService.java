package com.notification.gateway.service;

import com.notification.gateway.repository.UserRepository;

import com.notification.gateway.model.User;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    private final UserRepository userRepository;

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public User save(User user){
        return userRepository.save(user);
    }
}
