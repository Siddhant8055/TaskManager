package com.example.taskmanager.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.taskmanager.dto.RegisterRequest;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(RegisterRequest request) throws Exception {
        if(userRepository.existsByUsername(request.getUsername())){
            throw new Exception("Username already taken");
        }
        if(userRepository.existsByEmail(request.getEmail())){
            throw new Exception("Email already in use");
        }
        User u = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()));
        return userRepository.save(u);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
