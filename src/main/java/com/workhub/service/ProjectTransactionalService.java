package com.workhub.service;

import com.workhub.dto.CreateProjectWithTaskRequest;
import com.workhub.model.Project;
import com.workhub.model.TaskItem;
import com.workhub.repository.ProjectRepository;
import com.workhub.repository.TaskItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProjectTransactionalService {

    private final ProjectRepository projectRepository;
    private final TaskItemRepository taskItemRepository;

    public ProjectTransactionalService(ProjectRepository projectRepository, TaskItemRepository taskItemRepository) {
        this.projectRepository = projectRepository;
        this.taskItemRepository = taskItemRepository;
    }

    @Transactional
    public UUID createProjectWithInitialTask(CreateProjectWithTaskRequest request) {
        Project project = new Project();
        project.setTenantId(request.getTenantId());
        project.setName(request.getProjectName());
        project.setCreatedBy(request.getCreatedBy());
        Project savedProject = projectRepository.save(project);

        if (request.isSimulateFailure()) {
            throw new IllegalStateException("Simulated failure after project creation. Transaction must rollback.");
        }

        TaskItem taskItem = new TaskItem();
        taskItem.setTenantId(request.getTenantId());
        taskItem.setProjectId(savedProject.getId());
        taskItem.setTitle(request.getInitialTaskTitle());
        taskItem.setStatus("OPEN");
        taskItemRepository.save(taskItem);

        return savedProject.getId();
    }
}
