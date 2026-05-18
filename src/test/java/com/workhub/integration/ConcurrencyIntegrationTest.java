package com.workhub.integration;

import com.workhub.model.Project;
import com.workhub.model.TaskItem;
import com.workhub.model.User;
import com.workhub.repository.ProjectRepository;
import com.workhub.repository.TaskItemRepository;
import com.workhub.repository.UserRepository;
import com.workhub.security.TenantContext;
import com.workhub.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ConcurrencyIntegrationTest {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskItemRepository taskItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TransactionTemplate transactionTemplate;

    private UUID tenantId;
    private UUID taskId;

    @BeforeEach
    void setup() {
        taskItemRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        tenantId = UUID.randomUUID();

        User user = new User();
        user.setEmail("admin@tenant.com");
        user.setPasswordHash(passwordEncoder.encode("password"));
        user.setTenantId(tenantId);
        user.setRoles("TENANT_ADMIN");
        userRepository.save(user);

        Project project = new Project();
        project.setTenantId(tenantId);
        project.setName("Concurrent Project");
        project.setCreatedBy("admin@tenant.com");
        project = projectRepository.save(project);

        TaskItem task = new TaskItem();
        task.setTenantId(tenantId);
        task.setProjectId(project.getId());
        task.setTitle("Counter Task");
        task.setStatus("TODO");
        task.setCounter(0);
        task = taskItemRepository.save(task);
        taskId = task.getId();
    }

    @Test
    void concurrentIncrements_counterMatchesThreadCount() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger failures = new AtomicInteger();

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    TenantContext.setCurrentTenant(tenantId);
                    transactionTemplate.executeWithoutResult(status -> taskService.incrementCounter(taskId));
                } catch (Exception e) {
                    failures.incrementAndGet();
                } finally {
                    TenantContext.clear();
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        TaskItem finalTask = taskItemRepository.findById(taskId).orElseThrow();
        assertThat(failures.get()).isZero();
        assertThat(finalTask.getCounter()).isEqualTo(threadCount);
    }
}
