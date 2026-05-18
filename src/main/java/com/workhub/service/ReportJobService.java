package com.workhub.service;

import com.workhub.messaging.ReportJobMessage;
import com.workhub.messaging.ReportJobProducer;
import com.workhub.model.ReportJob;
import com.workhub.repository.ProjectRepository;
import com.workhub.repository.ReportJobRepository;
import com.workhub.security.TenantContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ReportJobService {

    private final ReportJobRepository reportJobRepository;
    private final ProjectRepository projectRepository;
    private final ReportJobProducer reportJobProducer;

    public ReportJobService(ReportJobRepository reportJobRepository,
                            ProjectRepository projectRepository,
                            org.springframework.beans.factory.ObjectProvider<ReportJobProducer> reportJobProducer) {
        this.reportJobRepository = reportJobRepository;
        this.projectRepository = projectRepository;
        this.reportJobProducer = reportJobProducer.getIfAvailable();
    }

    @Transactional
    public ReportJob enqueueReport(UUID projectId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set");
        }

        projectRepository.findByIdAndTenantId(projectId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        ReportJob job = new ReportJob();
        job.setTenantId(tenantId);
        job.setProjectId(projectId);
        job.setStatus("PENDING");
        job = reportJobRepository.save(job);

        if (reportJobProducer != null) {
            String messageId = UUID.randomUUID().toString();
            reportJobProducer.publish(new ReportJobMessage(job.getId(), tenantId, projectId, messageId));
        }
        return job;
    }

    public ReportJob getJob(UUID jobId) {
        UUID tenantId = TenantContext.getCurrentTenant();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set");
        }
        return reportJobRepository.findByIdAndTenantId(jobId, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Report job not found"));
    }
}
