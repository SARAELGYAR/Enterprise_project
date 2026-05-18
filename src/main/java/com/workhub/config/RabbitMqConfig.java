package com.workhub.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "workhub.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class RabbitMqConfig {

    @Bean
    public Queue reportQueue(@Value("${workhub.messaging.report-queue}") String queueName) {
        return new Queue(queueName, true);
    }

    @Bean
    public DirectExchange reportExchange(@Value("${workhub.messaging.report-exchange}") String exchangeName) {
        return new DirectExchange(exchangeName, true, false);
    }

    @Bean
    public Binding reportBinding(Queue reportQueue,
                                 DirectExchange reportExchange,
                                 @Value("${workhub.messaging.report-routing-key}") String routingKey) {
        return BindingBuilder.bind(reportQueue).to(reportExchange).with(routingKey);
    }

    @Bean
    public MessageConverter jacksonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
