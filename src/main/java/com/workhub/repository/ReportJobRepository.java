package com.workhub.repository;

import com.workhub.model.ReportJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReportJobRepository extends JpaRepository<ReportJob, UUID> {
    Optional<ReportJob> findByIdAndTenantId(UUID id, UUID tenantId);
}
