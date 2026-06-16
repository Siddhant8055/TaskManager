package com.example.taskmanager.service;

import java.util.List;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;

public interface TaskService {
    TaskResponse createTask(Long ownerId, TaskRequest request);
    List<TaskResponse> getTasksForOwner(Long ownerId);
    List<TaskResponse> getTasksForOwner(Long ownerId, String q, String status, String priority);
    TaskResponse getTask(Long id, Long ownerId);
    TaskResponse updateTask(Long id, Long ownerId, TaskRequest request);
    void deleteTask(Long id, Long ownerId);
}
