package com.workhub.messaging;

import com.workhub.model.ProcessedMessage;
import com.workhub.model.Project;
import com.workhub.model.ReportJob;
import com.workhub.repository.ProcessedMessageRepository;
import com.workhub.repository.ProjectRepository;
import com.workhub.repository.ReportJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = "workhub.messaging.enabled=true")
class MessagingReliabilityIntegrationTest {

  static boolean dockerAvailable() {
    return DockerClientFactory.instance().isDockerAvailable();
  }

  @Container
  static RabbitMQContainer rabbitMq = new RabbitMQContainer("rabbitmq:3.12-management-alpine");

  @DynamicPropertySource
  static void rabbitProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.rabbitmq.host", rabbitMq::getHost);
    registry.add("spring.rabbitmq.port", rabbitMq::getAmqpPort);
    registry.add("spring.rabbitmq.username", rabbitMq::getAdminUsername);
    registry.add("spring.rabbitmq.password", rabbitMq::getAdminPassword);
  }

  @Autowired
  private ReportJobProducer reportJobProducer;

  @Autowired
  private ReportJobRepository reportJobRepository;

  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private ProcessedMessageRepository processedMessageRepository;

  private UUID tenantId;
  private UUID projectId;
  private UUID jobId;
  private String messageId;

  @BeforeEach
  void setup() {
    processedMessageRepository.deleteAll();
    reportJobRepository.deleteAll();
    projectRepository.deleteAll();

    tenantId = UUID.randomUUID();

    Project project = new Project();
    project.setTenantId(tenantId);
    project.setName("Messaging Project");
    project.setCreatedBy("admin@tenant.com");
    project = projectRepository.save(project);
    projectId = project.getId();

    ReportJob job = new ReportJob();
    job.setTenantId(tenantId);
    job.setProjectId(projectId);
    job.setStatus("PENDING");
    job = reportJobRepository.save(job);
    jobId = job.getId();
    messageId = UUID.randomUUID().toString();
  }

  @Test
  @EnabledIf("com.workhub.messaging.MessagingReliabilityIntegrationTest#dockerAvailable")
  void duplicateMessages_processedOnce_jobCompleted() {
    ReportJobMessage message = new ReportJobMessage(jobId, tenantId, projectId, messageId);

    reportJobProducer.publish(message);
    reportJobProducer.publish(message);

    await().atMost(30, TimeUnit.SECONDS).untilAsserted(() -> {
      ReportJob job = reportJobRepository.findById(jobId).orElseThrow();
      assertThat(job.getStatus()).isEqualTo("COMPLETED");
      assertThat(processedMessageRepository.findById(messageId)).isPresent();
      assertThat(processedMessageRepository.count()).isEqualTo(1);
    });
  }
}
