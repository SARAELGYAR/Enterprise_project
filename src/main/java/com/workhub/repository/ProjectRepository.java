package com.workhub.repository;

import com.workhub.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {
    // Tenant-isolated queries
    java.util.List<Project> findByTenantId(UUID tenantId);
    Optional<Project> findByIdAndTenantId(UUID id, UUID tenantId);
}
