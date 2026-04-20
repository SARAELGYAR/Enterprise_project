package com.workhub.dto;

import java.util.UUID;

public class UserResponse {
    private UUID id;
    private String email;
    private UUID tenantId;
    private String roles;

    public UserResponse() {}

    public UserResponse(UUID id, String email, UUID tenantId, String roles) {
        this.id = id;
        this.email = email;
        this.tenantId = tenantId;
        this.roles = roles;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
