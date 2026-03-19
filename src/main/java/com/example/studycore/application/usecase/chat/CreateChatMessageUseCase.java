package com.example.studycore.application.usecase.chat;

import com.example.studycore.domain.model.ChatMessage;
import com.example.studycore.domain.port.ChatMessageGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateChatMessageUseCase {

    private final ChatMessageGateway chatMessageGateway;

    public ChatMessage execute(UUID activityId, UUID userId, String content) {
        ChatMessage message = new ChatMessage();
        message.setId(UUID.randomUUID());
        message.setActivityId(activityId);
        message.setUserId(userId);
        message.setContent(content);
        message.setSentAt(OffsetDateTime.now());

        return chatMessageGateway.save(message);
    }
}

