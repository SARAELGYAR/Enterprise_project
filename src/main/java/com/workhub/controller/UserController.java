package com.workhub.controller;

import com.workhub.model.User;
import com.workhub.repository.UserRepository;
import com.workhub.security.TenantContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasRole('TENANT_ADMIN') or hasRole('MEMBER')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        UUID tenantId = TenantContext.getCurrentTenant();
        return ResponseEntity.ok(userRepository.findByTenantId(tenantId));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @GetMapping("/{id}")
    public User getUserById(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getCurrentTenant();
        return userRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @PreAuthorize("hasRole('TENANT_ADMIN')")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        UUID tenantId = TenantContext.getCurrentTenant();
        user.setTenantId(tenantId);
        try {
            return ResponseEntity.ok(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
    }
}