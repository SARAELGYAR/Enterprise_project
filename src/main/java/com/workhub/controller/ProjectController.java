package com.workhub.controller;

import com.workhub.model.Project;
import com.workhub.model.ReportJob;
import com.workhub.model.TaskItem;
import com.workhub.service.ProjectService;
import com.workhub.service.ReportJobService;
import com.workhub.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final ReportJobService reportJobService;

    public ProjectController(ProjectService projectService,
                            TaskService taskService,
                            ReportJobService reportJobService) {
        this.projectService = projectService;
        this.taskService = taskService;
        this.reportJobService = reportJobService;
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @PostMapping
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project project) {
        return ResponseEntity.status(201).body(projectService.createProject(project));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN') or hasRole('TENANT_USER')")
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @PreAuthorize("hasRole('TENANT_ADMIN') or hasRole('TENANT_USER')")
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN') or hasRole('TENANT_USER')")
    @PostMapping("/{id}/tasks")
    public ResponseEntity<TaskItem> createTaskForProject(@PathVariable UUID id, @Valid @RequestBody TaskItem task) {
        return ResponseEntity.status(201).body(taskService.createTaskForProject(id, task));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN') or hasRole('TENANT_USER')")
    @PostMapping("/{id}/generate-report")
    public ResponseEntity<ReportJob> generateReport(@PathVariable UUID id) {
        return ResponseEntity.accepted().body(reportJobService.enqueueReport(id));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Project> updateProject(@PathVariable UUID id, @Valid @RequestBody Project projectDetails) {
        return ResponseEntity.ok(projectService.updateProject(id, projectDetails));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
