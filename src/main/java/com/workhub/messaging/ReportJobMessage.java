package com.workhub.messaging;

import java.io.Serializable;
import java.util.UUID;

public class ReportJobMessage implements Serializable {

    private UUID jobId;
    private UUID tenantId;
    private UUID projectId;
    private String messageId;

    public ReportJobMessage() {
    }

    public ReportJobMessage(UUID jobId, UUID tenantId, UUID projectId, String messageId) {
        this.jobId = jobId;
        this.tenantId = tenantId;
        this.projectId = projectId;
        this.messageId = messageId;
    }

    public UUID getJobId() {
        return jobId;
    }

    public void setJobId(UUID jobId) {
        this.jobId = jobId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
