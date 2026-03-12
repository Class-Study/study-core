package com.example.studycore.infrastructure.facade;

import com.example.studycore.application.facades.MessageFacade;
import com.example.studycore.application.messages.NotifyUserMessage;
import com.example.studycore.infrastructure.gateway.messaging.producer.NotifyProducer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MessageFacadeImpl implements MessageFacade {

    private final NotifyProducer notifyProducer;

    @Override
    public void sendMessage(NotifyUserMessage message) {
        notifyProducer.send(message);
    }
}
