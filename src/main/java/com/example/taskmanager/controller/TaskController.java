package com.example.taskmanager.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.service.TaskService;
import com.example.taskmanager.service.UserService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    private User getCurrentUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal != null ? principal.toString() : null;
        if(username==null) return null;
        return userService.findByUsername(username);
    }

    @GetMapping
    public ResponseEntity<?> listTasks(@RequestParam(required = false) String q,
                                       @RequestParam(required = false) String status,
                                       @RequestParam(required = false) String priority){
        User u = getCurrentUser();
        if(u==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthenticated");
        List<TaskResponse> tasks = taskService.getTasksForOwner(u.getId(), q, status, priority);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id){
        User u = getCurrentUser();
        if(u==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthenticated");
        try{
            TaskResponse t = taskService.getTask(id, u.getId());
            return ResponseEntity.ok(t);
        } catch (RuntimeException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody TaskRequest request){
        User u = getCurrentUser();
        if(u==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthenticated");
        TaskResponse created = taskService.createTask(u.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody TaskRequest request){
        User u = getCurrentUser();
        if(u==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthenticated");
        try{
            TaskResponse updated = taskService.updateTask(id, u.getId(), request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id){
        User u = getCurrentUser();
        if(u==null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthenticated");
        try{
            taskService.deleteTask(id, u.getId());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException ex){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }
}
