package com.workhub.repository;

import com.workhub.model.TaskItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskItemRepository extends JpaRepository<TaskItem, UUID> {
    // Tenant-isolated queries
    java.util.List<TaskItem> findByTenantId(UUID tenantId);
    java.util.List<TaskItem> findByProjectIdAndTenantId(UUID projectId, UUID tenantId);
    Optional<TaskItem> findByIdAndTenantId(UUID id, UUID tenantId);
}
