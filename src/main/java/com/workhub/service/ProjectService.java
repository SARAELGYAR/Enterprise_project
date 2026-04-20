package com.workhub.service;

import com.workhub.model.Project;
import com.workhub.repository.ProjectRepository;
import com.workhub.security.TenantContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getAllProjects() {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set");
        }
        return projectRepository.findByTenantId(tenantId);
    }

    public Project getProjectById(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set");
        }
        return projectRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
    }

    public Project createProject(Project project) {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set");
        }
        project.setTenantId(tenantId);
        return projectRepository.save(project);
    }

    public Project updateProject(UUID id, Project projectDetails) {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set");
        }
        
        Project project = projectRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        project.setName(projectDetails.getName());
        // Add other updatable fields as needed
        
        return projectRepository.save(project);
    }

    public void deleteProject(UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set");
        }
        
        Project project = projectRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        
        projectRepository.delete(project);
    }
}