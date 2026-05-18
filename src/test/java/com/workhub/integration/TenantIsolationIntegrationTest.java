package com.workhub.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workhub.model.Project;
import com.workhub.model.TaskItem;
import com.workhub.model.User;
import com.workhub.repository.ProjectRepository;
import com.workhub.repository.TaskItemRepository;
import com.workhub.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TenantIsolationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskItemRepository taskItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private UUID tenantAId;
    private String tenantAToken;
    private UUID tenantBProjectId;
    private UUID tenantBTaskId;

    @BeforeEach
    void setup() {
        taskItemRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        tenantAId = UUID.randomUUID();
        UUID tenantBId = UUID.randomUUID();

        User userA = new User();
        userA.setEmail("admin@tenant-a.com");
        userA.setPasswordHash(passwordEncoder.encode("password"));
        userA.setTenantId(tenantAId);
        userA.setRoles("TENANT_ADMIN");
        userRepository.save(userA);

        User userB = new User();
        userB.setEmail("admin@tenant-b.com");
        userB.setPasswordHash(passwordEncoder.encode("password"));
        userB.setTenantId(tenantBId);
        userB.setRoles("TENANT_ADMIN");
        userRepository.save(userB);

        tenantAToken = generateToken(userA.getEmail(), tenantAId, "TENANT_ADMIN");

        Project tenantBProject = new Project();
        tenantBProject.setTenantId(tenantBId);
        tenantBProject.setName("Tenant B Project");
        tenantBProject.setCreatedBy("admin@tenant-b.com");
        tenantBProject = projectRepository.save(tenantBProject);
        tenantBProjectId = tenantBProject.getId();

        TaskItem tenantBTask = new TaskItem();
        tenantBTask.setTenantId(tenantBId);
        tenantBTask.setProjectId(tenantBProjectId);
        tenantBTask.setTitle("Tenant B Task");
        tenantBTask.setStatus("TODO");
        tenantBTask = taskItemRepository.save(tenantBTask);
        tenantBTaskId = tenantBTask.getId();
    }

    @Test
    @Transactional
    void crossTenantRead_project_returns404() throws Exception {
        mockMvc.perform(get("/projects/" + tenantBProjectId)
                        .header("Authorization", "Bearer " + tenantAToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void crossTenantUpdate_task_returns404() throws Exception {
        Map<String, String> patch = Map.of("status", "IN_PROGRESS");

        mockMvc.perform(patch("/tasks/" + tenantBTaskId)
                        .header("Authorization", "Bearer " + tenantAToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patch)))
                .andExpect(status().isNotFound());

        TaskItem task = taskItemRepository.findById(tenantBTaskId).orElseThrow();
        assertThat(task.getStatus()).isEqualTo("TODO");
    }

    @Test
    @Transactional
    void crossTenantList_projects_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/projects")
                        .header("Authorization", "Bearer " + tenantAToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    private String generateToken(String email, UUID tenantId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", email);
        claims.put("tenantId", tenantId.toString());
        claims.put("roles", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256)
                .compact();
    }
}
