package com.example.taskmanager.service;

import com.example.taskmanager.dto.RegisterRequest;
import com.example.taskmanager.entity.User;

public interface UserService {
    User register(RegisterRequest request) throws Exception;
    User findByUsername(String username);
}
