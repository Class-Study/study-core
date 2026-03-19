package com.example.studycore.application.usecase.chat;

import com.example.studycore.domain.model.ChatMessage;
import com.example.studycore.domain.port.ChatMessageGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListChatMessagesUseCase {

    private final ChatMessageGateway chatMessageGateway;

    public List<ChatMessage> execute(UUID activityId) {
        return chatMessageGateway.findByActivityId(activityId);
    }
}

