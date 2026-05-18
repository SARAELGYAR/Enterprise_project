package com.workhub.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.workhub.model.Project;
import com.workhub.model.User;
import com.workhub.repository.ProjectRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RbacIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private UUID tenantId;
    private String adminToken;
    private String userToken;

    @BeforeEach
    void setup() {
        projectRepository.deleteAll();
        userRepository.deleteAll();

        tenantId = UUID.randomUUID();

        User admin = new User();
        admin.setEmail("admin@tenant.com");
        admin.setPasswordHash(passwordEncoder.encode("password"));
        admin.setTenantId(tenantId);
        admin.setRoles("TENANT_ADMIN");
        userRepository.save(admin);

        User user = new User();
        user.setEmail("user@tenant.com");
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setTenantId(tenantId);
        user.setRoles("TENANT_USER");
        userRepository.save(user);

        adminToken = generateToken("admin@tenant.com", tenantId, "TENANT_ADMIN");
        userToken = generateToken("user@tenant.com", tenantId, "TENANT_USER");
    }

    @Test
    @Transactional
    void missingToken_returns401() throws Exception {
        Project project = new Project();
        project.setTenantId(tenantId);
        project.setName("Test Project");
        project.setCreatedBy("admin@tenant.com");

        mockMvc.perform(post("/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void wrongRole_returns403() throws Exception {
        Project project = new Project();
        project.setTenantId(tenantId);
        project.setName("Test Project");
        project.setCreatedBy("user@tenant.com");

        mockMvc.perform(post("/projects")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void adminAllowed_returns201() throws Exception {
        Project project = new Project();
        project.setTenantId(tenantId);
        project.setName("Test Project");
        project.setCreatedBy("admin@tenant.com");

        mockMvc.perform(post("/projects")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(project)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Project"));
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
