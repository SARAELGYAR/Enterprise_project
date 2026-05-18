package com.workhub.messaging;

import com.workhub.model.ProcessedMessage;
import com.workhub.model.ReportJob;
import com.workhub.repository.ProcessedMessageRepository;
import com.workhub.repository.ReportJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
@ConditionalOnProperty(name = "workhub.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class ReportJobConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ReportJobConsumer.class);

    private final ReportJobRepository reportJobRepository;
    private final ProcessedMessageRepository processedMessageRepository;

    public ReportJobConsumer(ReportJobRepository reportJobRepository,
                             ProcessedMessageRepository processedMessageRepository) {
        this.reportJobRepository = reportJobRepository;
        this.processedMessageRepository = processedMessageRepository;
    }

    @RabbitListener(queues = "${workhub.messaging.report-queue}")
    @Transactional
    public void consume(ReportJobMessage message) {
        if (processedMessageRepository.existsById(message.getMessageId())) {
            logger.info("Skipping duplicate messageId={}", message.getMessageId());
            return;
        }

        ReportJob job = reportJobRepository.findById(message.getJobId())
                .orElseThrow(() -> new IllegalStateException("Report job not found: " + message.getJobId()));

        job.setStatus("COMPLETED");
        job.setOutput("Report generated for project " + job.getProjectId());
        job.setCompletedAt(Instant.now());
        reportJobRepository.save(job);
        processedMessageRepository.save(new ProcessedMessage(message.getMessageId()));

        logger.info("Report job completed jobId={} messageId={}", job.getId(), message.getMessageId());
    }
}
