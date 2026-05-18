package com.workhub.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "workhub.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class ReportJobProducer {

    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public ReportJobProducer(RabbitTemplate rabbitTemplate,
                             @Value("${workhub.messaging.report-exchange}") String exchange,
                             @Value("${workhub.messaging.report-routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publish(ReportJobMessage message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
