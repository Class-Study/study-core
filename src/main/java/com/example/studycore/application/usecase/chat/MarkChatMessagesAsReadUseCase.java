package com.example.studycore.application.usecase.chat;

import com.example.studycore.domain.port.ChatMessageGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MarkChatMessagesAsReadUseCase {

    private final ChatMessageGateway chatMessageGateway;

    public void execute(UUID activityId, UUID userId) {
        chatMessageGateway.markAsRead(activityId, userId);
    }
}

