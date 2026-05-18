package com.workhub.repository;

import com.workhub.model.TaskItem;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskItemRepository extends JpaRepository<TaskItem, UUID> {
    java.util.List<TaskItem> findByTenantId(UUID tenantId);
    java.util.List<TaskItem> findByProjectIdAndTenantId(UUID projectId, UUID tenantId);
    Optional<TaskItem> findByIdAndTenantId(UUID id, UUID tenantId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT t FROM TaskItem t WHERE t.id = :id AND t.tenantId = :tenantId")
    Optional<TaskItem> findByIdAndTenantIdForUpdate(@Param("id") UUID id, @Param("tenantId") UUID tenantId);
}
