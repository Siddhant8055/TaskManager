package com.example.taskmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskStatus;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOwnerId(Long ownerId);
    List<Task> findByTitleContainingIgnoreCaseAndOwnerId(String title, Long ownerId);
    List<Task> findByStatusAndOwnerId(TaskStatus status, Long ownerId);
}
