package com.workhub.service;

import com.workhub.model.TaskItem;
import com.workhub.repository.TaskItemRepository;
import com.workhub.security.TenantContext;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TaskService {

    private static final int MAX_RETRIES = 25;

    private final TaskItemRepository taskItemRepository;
    private final ProjectService projectService;

    public TaskService(TaskItemRepository taskItemRepository, ProjectService projectService) {
        this.taskItemRepository = taskItemRepository;
        this.projectService = projectService;
    }

    public List<TaskItem> getAllTasks() {
        UUID tenantId = requireTenant();
        return taskItemRepository.findByTenantId(tenantId);
    }

    public TaskItem getTaskById(UUID id) {
        UUID tenantId = requireTenant();
        return taskItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    public TaskItem createTask(TaskItem task) {
        UUID tenantId = requireTenant();
        projectService.getProjectById(task.getProjectId());
        task.setTenantId(tenantId);
        return taskItemRepository.save(task);
    }

    public TaskItem createTaskForProject(UUID projectId, TaskItem task) {
        task.setProjectId(projectId);
        return createTask(task);
    }

    public TaskItem updateTask(UUID id, TaskItem taskDetails) {
        UUID tenantId = requireTenant();
        TaskItem task = taskItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        task.setTitle(taskDetails.getTitle());
        task.setStatus(taskDetails.getStatus());
        if (taskDetails.getProjectId() != null) {
            task.setProjectId(taskDetails.getProjectId());
        }
        return taskItemRepository.save(task);
    }

    public TaskItem patchTask(UUID id, Map<String, Object> updates) {
        UUID tenantId = requireTenant();
        TaskItem task = taskItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));

        if (updates.containsKey("title")) {
            task.setTitle(String.valueOf(updates.get("title")));
        }
        if (updates.containsKey("status")) {
            task.setStatus(String.valueOf(updates.get("status")));
        }
        return taskItemRepository.save(task);
    }

    @Transactional
    public TaskItem incrementCounter(UUID id) {
        UUID tenantId = requireTenant();
        TaskItem task = taskItemRepository.findByIdAndTenantIdForUpdate(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        task.setCounter(task.getCounter() + 1);
        return taskItemRepository.save(task);
    }

    @Transactional
    public TaskItem patchTaskWithOptimisticLock(UUID id, Map<String, Object> updates) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                return patchTask(id, updates);
            } catch (OptimisticLockingFailureException ex) {
                if (attempt == MAX_RETRIES - 1) {
                    throw ex;
                }
            }
        }
        throw new IllegalStateException("Unable to patch task after retries");
    }

    public void deleteTask(UUID id) {
        UUID tenantId = requireTenant();
        TaskItem task = taskItemRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        taskItemRepository.delete(task);
    }

    private UUID requireTenant() {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set");
        }
        return tenantId;
    }
}
