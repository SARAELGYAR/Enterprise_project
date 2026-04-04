package com.workhub.service;

import com.workhub.dto.CreateProjectWithTaskRequest;
import com.workhub.repository.ProjectRepository;
import com.workhub.repository.TaskItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(ProjectTransactionalService.class)
class ProjectTransactionalServiceTest {

    @Autowired
    private ProjectTransactionalService projectTransactionalService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TaskItemRepository taskItemRepository;

    @BeforeEach
    void cleanDatabase() {
        taskItemRepository.deleteAll();
        projectRepository.deleteAll();
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void createProjectWithInitialTask_success_persistsBothRecords() {
        CreateProjectWithTaskRequest request = new CreateProjectWithTaskRequest();
        request.setTenantId(UUID.randomUUID());
        request.setProjectName("Phase 1 Demo");
        request.setCreatedBy("admin@tenant-a.com");
        request.setInitialTaskTitle("Create first task");
        request.setSimulateFailure(false);

        projectTransactionalService.createProjectWithInitialTask(request);

        assertThat(projectRepository.count()).isEqualTo(1);
        assertThat(taskItemRepository.count()).isEqualTo(1);
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void createProjectWithInitialTask_failure_rollsBackProjectAndTask() {
        CreateProjectWithTaskRequest request = new CreateProjectWithTaskRequest();
        request.setTenantId(UUID.randomUUID());
        request.setProjectName("Rollback Demo");
        request.setCreatedBy("admin@tenant-a.com");
        request.setInitialTaskTitle("Task should not persist");
        request.setSimulateFailure(true);

        assertThatThrownBy(() -> projectTransactionalService.createProjectWithInitialTask(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Simulated failure");

        assertThat(projectRepository.count()).isZero();
        assertThat(taskItemRepository.count()).isZero();
    }
}
