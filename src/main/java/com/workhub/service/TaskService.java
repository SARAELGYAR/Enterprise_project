package com.workhub.service;

import com.workhub.model.TaskItem;
import com.workhub.repository.TaskItemRepository;
import com.workhub.security.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    private final TaskItemRepository taskItemRepository;

    public TaskService(TaskItemRepository taskItemRepository) {
        this.taskItemRepository = taskItemRepository;
    }

    public List<TaskItem> getAllTasks() {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set");
        }
        return taskItemRepository.findByTenantId(tenantId);
    }

    public TaskItem getTaskById(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set");
        }
        return taskItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    public TaskItem createTask(TaskItem task) {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set");
        }
        task.setTenantId(tenantId);
        return taskItemRepository.save(task);
    }

    public TaskItem updateTask(UUID id, TaskItem taskDetails) {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set");
        }
        
        TaskItem task = taskItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        
        task.setTitle(taskDetails.getTitle());
        task.setStatus(taskDetails.getStatus());
        task.setProjectId(taskDetails.getProjectId());
        
        return taskItemRepository.save(task);
    }

    public void deleteTask(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set");
        }
        
        TaskItem task = taskItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        
        taskItemRepository.delete(task);
    }
}
