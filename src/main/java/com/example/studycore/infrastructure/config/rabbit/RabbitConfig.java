package com.example.studycore.infrastructure.config.rabbit;

import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        template.setChannelTransacted(true);
        return template;
    }

    @Bean
    public Declarables declarableNotify(
            @Value("${amqp.queue.notify-user}") String notifyUserQueue,
            @Value("${amqp.dlq.notify-user-dlq}") String notifyUserDlq) {

        final Queue mainQueue = QueueBuilder.durable(notifyUserQueue)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", notifyUserDlq)
                .build();
        final Queue dlq = QueueBuilder.durable(notifyUserDlq).build();

        return new Declarables(mainQueue, dlq);
    }
}
