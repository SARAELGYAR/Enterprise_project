package com.workhub.repository;

import com.workhub.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    
    // Tenant-isolated queries
    Optional<User> findByEmailAndTenantId(String email, UUID tenantId);
    java.util.List<User> findByTenantId(UUID tenantId);
}
