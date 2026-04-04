package com.workhub.controller;

import com.workhub.dto.CreateProjectWithTaskRequest;
import com.workhub.service.ProjectTransactionalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/transaction-demo")
public class TransactionDemoController {

    private final ProjectTransactionalService projectTransactionalService;

    public TransactionDemoController(ProjectTransactionalService projectTransactionalService) {
        this.projectTransactionalService = projectTransactionalService;
    }

    @PostMapping("/projects")
    public ResponseEntity<Map<String, Object>> createProjectWithTask(@Valid @RequestBody CreateProjectWithTaskRequest request) {
        UUID projectId = projectTransactionalService.createProjectWithInitialTask(request);
        Map<String, Object> response = new HashMap<>();
        response.put("projectId", projectId);
        response.put("message", "Project and initial task created in one transaction");
        return ResponseEntity.ok(response);
    }
}
