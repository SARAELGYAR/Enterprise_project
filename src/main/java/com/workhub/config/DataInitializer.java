package com.workhub.config;

import com.workhub.model.Tenant;
import com.workhub.model.User;
import com.workhub.repository.TenantRepository;
import com.workhub.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(TenantRepository tenantRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            // --- TENANT 1 ---
            Tenant tenant1;
            if (!tenantRepository.existsByName("Tenant One")) {
                tenant1 = new Tenant();
                tenant1.setName("Tenant One");
                tenant1.setPlan("PREMIUM");
                tenant1 = tenantRepository.save(tenant1);

                // Admin user for tenant 1
                User admin1 = new User();
                admin1.setEmail("admin@tenant1.com");
                admin1.setPasswordHash(passwordEncoder.encode("password123"));
                admin1.setTenantId(tenant1.getId());
                admin1.setRoles("TENANT_ADMIN");
                userRepository.save(admin1);

                // Member user for tenant 1
                User member1 = new User();
                member1.setEmail("member@tenant1.com");
                member1.setPasswordHash(passwordEncoder.encode("password123"));
                member1.setTenantId(tenant1.getId());
                member1.setRoles("MEMBER");
                userRepository.save(member1);

                System.out.println("Tenant 1 ID: " + tenant1.getId());
            }

            // --- TENANT 2 ---
            if (!tenantRepository.existsByName("Tenant Two")) {
                Tenant tenant2 = new Tenant();
                tenant2.setName("Tenant Two");
                tenant2.setPlan("BASIC");
                tenant2 = tenantRepository.save(tenant2);

                User admin2 = new User();
                admin2.setEmail("admin@tenant2.com");
                admin2.setPasswordHash(passwordEncoder.encode("password123"));
                admin2.setTenantId(tenant2.getId());
                admin2.setRoles("TENANT_ADMIN");
                userRepository.save(admin2);

                System.out.println("Tenant 2 ID: " + tenant2.getId());
            }
        };
    }
}
