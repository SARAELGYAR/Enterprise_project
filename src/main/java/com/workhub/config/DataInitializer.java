package com.workhub.config;

import com.workhub.model.Tenant;
import com.workhub.model.User;
import com.workhub.repository.TenantRepository;
import com.workhub.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(TenantRepository tenantRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create test tenant
            Tenant tenant = new Tenant();
            tenant.setName("Test Organization");
            tenant.setPlan("PREMIUM");
            tenant = tenantRepository.save(tenant);

            // Create test user
            User user = new User();
            user.setEmail("user@example.com");
            user.setPasswordHash(passwordEncoder.encode("password123"));
            user.setTenantId(tenant.getId());
            user.setRoles("TENANT_ADMIN");
            userRepository.save(user);

            System.out.println("Test data initialized:");
            System.out.println("Tenant ID: " + tenant.getId());
            System.out.println("User email: user@example.com");
            System.out.println("User password: password123");
        };
    }
}
