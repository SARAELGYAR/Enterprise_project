package com.workhub.controller;

import com.workhub.model.TaskItem;
import com.workhub.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @PostMapping
    public ResponseEntity<TaskItem> createTask(@Valid @RequestBody TaskItem task) {
        return ResponseEntity.ok(taskService.createTask(task));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN') or hasRole('MEMBER')")
    @GetMapping
    public ResponseEntity<List<TaskItem>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @PreAuthorize("hasRole('TENANT_ADMIN') or hasRole('MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<TaskItem> getTaskById(@PathVariable UUID id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<TaskItem> updateTask(@PathVariable UUID id, @Valid @RequestBody TaskItem taskDetails) {
        return ResponseEntity.ok(taskService.updateTask(id, taskDetails));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
