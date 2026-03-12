package com.example.studycore.infrastructure.gateway.messaging.producer;

import com.example.studycore.application.messages.NotifyUserMessage;
import com.example.studycore.infrastructure.gateway.messaging.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotifyProducer extends Message<NotifyUserMessage> {

    private final AmqpTemplate amqpTemplate;
    private final String queueName;

    public NotifyProducer(
            AmqpTemplate amqpTemplate,
            @Value("${amqp.queue.notify-user}") String queueName) {
        this.amqpTemplate = amqpTemplate;
        this.queueName = queueName;
    }

    @Override
    public void send(NotifyUserMessage message) {
        try {
            this.amqpTemplate.convertAndSend(this.queueName, message);
            log.info("Notification message sent successfully to queue: {}, email: {}", queueName, message.email());
        } catch (Exception e) {
            log.error("Failed to send notification message for email: {}", message.email(), e);
            throw new RuntimeException("Failed to send notification message", e);
        }
    }
}
