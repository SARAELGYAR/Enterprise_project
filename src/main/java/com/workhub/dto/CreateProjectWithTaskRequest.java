package com.workhub.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class CreateProjectWithTaskRequest {

    @NotNull
    private UUID tenantId;

    @NotBlank
    private String projectName;

    @NotBlank
    private String createdBy;

    @NotBlank
    private String initialTaskTitle;

    private boolean simulateFailure;

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getInitialTaskTitle() {
        return initialTaskTitle;
    }

    public void setInitialTaskTitle(String initialTaskTitle) {
        this.initialTaskTitle = initialTaskTitle;
    }

    public boolean isSimulateFailure() {
        return simulateFailure;
    }

    public void setSimulateFailure(boolean simulateFailure) {
        this.simulateFailure = simulateFailure;
    }
}
