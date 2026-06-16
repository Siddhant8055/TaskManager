package com.example.taskmanager.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.taskmanager.dto.TaskRequest;
import com.example.taskmanager.dto.TaskResponse;
import com.example.taskmanager.entity.Task;
import com.example.taskmanager.entity.TaskPriority;
import com.example.taskmanager.entity.TaskStatus;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import com.example.taskmanager.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private TaskResponse toDto(Task t){
        TaskResponse r = new TaskResponse();
        r.setId(t.getId()); r.setTitle(t.getTitle()); r.setDescription(t.getDescription());
        r.setPriority(t.getPriority().name()); r.setStatus(t.getStatus().name());
        r.setCreatedAt(t.getCreatedAt()); r.setUpdatedAt(t.getUpdatedAt()); r.setDueDate(t.getDueDate());
        return r;
    }

    @Override
    public TaskResponse createTask(Long ownerId, TaskRequest request) {
        User owner = userRepository.findById(ownerId).orElseThrow();
        Task t = new Task();
        t.setTitle(request.getTitle());
        t.setDescription(request.getDescription());
        t.setPriority(request.getPriority() != null ? TaskPriority.valueOf(request.getPriority()) : TaskPriority.MEDIUM);
        t.setStatus(request.getStatus() != null ? TaskStatus.valueOf(request.getStatus()) : TaskStatus.PENDING);
        t.setOwner(owner);
        Task saved = taskRepository.save(t);
        return toDto(saved);
    }

    @Override
    public List<TaskResponse> getTasksForOwner(Long ownerId) {
        return getTasksForOwner(ownerId, null, null, null);
    }

    @Override
    public List<TaskResponse> getTasksForOwner(Long ownerId, String q, String status, String priority) {
        List<Task> list = taskRepository.findByOwnerId(ownerId);
        return list.stream().filter(t -> {
            if(q != null && !q.isBlank()){
                String low = q.toLowerCase();
                boolean inTitle = t.getTitle()!=null && t.getTitle().toLowerCase().contains(low);
                boolean inDesc = t.getDescription()!=null && t.getDescription().toLowerCase().contains(low);
                if(!(inTitle || inDesc)) return false;
            }
            if(status != null && !status.isBlank()){
                if(!t.getStatus().name().equalsIgnoreCase(status)) return false;
            }
            if(priority != null && !priority.isBlank()){
                if(!t.getPriority().name().equalsIgnoreCase(priority)) return false;
            }
            return true;
        }).map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public TaskResponse getTask(Long id, Long ownerId) {
        Task t = taskRepository.findById(id).orElseThrow();
        if(t.getOwner()==null || !t.getOwner().getId().equals(ownerId)) throw new RuntimeException("Not authorized");
        return toDto(t);
    }

    @Override
    public TaskResponse updateTask(Long id, Long ownerId, TaskRequest request) {
        Task t = taskRepository.findById(id).orElseThrow();
        if(t.getOwner()==null || !t.getOwner().getId().equals(ownerId)) throw new RuntimeException("Not authorized");
        if(request.getTitle()!=null) t.setTitle(request.getTitle());
        if(request.getDescription()!=null) t.setDescription(request.getDescription());
        if(request.getPriority()!=null) t.setPriority(TaskPriority.valueOf(request.getPriority()));
        if(request.getStatus()!=null) t.setStatus(TaskStatus.valueOf(request.getStatus()));
        Task saved = taskRepository.save(t);
        return toDto(saved);
    }

    @Override
    public void deleteTask(Long id, Long ownerId) {
        Task t = taskRepository.findById(id).orElseThrow();
        if(t.getOwner()==null || !t.getOwner().getId().equals(ownerId)) throw new RuntimeException("Not authorized");
        taskRepository.delete(t);
    }
}
