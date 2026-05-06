package com.workhub.controller;

import com.workhub.model.Project;
import com.workhub.service.ProjectService;
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

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @PostMapping
    public ResponseEntity<Project> createProject(@Valid @RequestBody Project project) {
        return ResponseEntity.ok(projectService.createProject(project));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN') or hasRole('MEMBER')")
    @GetMapping
    public ResponseEntity<List<Project>> getAllProjects() {
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @PreAuthorize("hasRole('TENANT_ADMIN') or hasRole('MEMBER')")
    @GetMapping("/{id}")
    public ResponseEntity<Project> getProjectById(@PathVariable UUID id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
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
