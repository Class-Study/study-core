package com.example.studycore.infrastructure.api.controllers.chat;

import com.example.studycore.application.usecase.chat.CreateChatMessageUseCase;
import com.example.studycore.application.usecase.chat.ListChatMessagesUseCase;
import com.example.studycore.application.usecase.chat.MarkChatMessagesAsReadUseCase;
import com.example.studycore.infrastructure.api.ChatApi;
import com.example.studycore.infrastructure.api.controllers.chat.request.CreateChatMessageRequest;
import com.example.studycore.infrastructure.api.controllers.chat.request.MarkChatMessagesAsReadRequest;
import com.example.studycore.infrastructure.api.controllers.chat.response.ChatMessageResponse;
import com.example.studycore.infrastructure.mapper.ChatInfraMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController implements ChatApi {

    private final ListChatMessagesUseCase listChatMessagesUseCase;
    private final CreateChatMessageUseCase createChatMessageUseCase;
    private final MarkChatMessagesAsReadUseCase markChatMessagesAsReadUseCase;

    private static final ChatInfraMapper CHAT_MAPPER = ChatInfraMapper.INSTANCE;

    @Override
    public ResponseEntity<List<ChatMessageResponse>> listMessages(@PathVariable UUID activityId) {
        log.info("[Chat] Listando mensagens | activityId={}", activityId);

        var messages = listChatMessagesUseCase.execute(activityId);
        var response = CHAT_MAPPER.toChatMessageResponseList(messages);

        log.debug("[Chat] {} mensagens retornadas | activityId={}", response.size(), activityId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ChatMessageResponse> createMessage(
            @PathVariable UUID activityId,
            @RequestBody CreateChatMessageRequest request) {

        log.info("[Chat] Criando mensagem | activityId={} | userId={}",
                activityId, request.getUserId());

        var message = createChatMessageUseCase.execute(activityId, request.getUserId(), request.getContent());
        var response = CHAT_MAPPER.toChatMessageResponse(message);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> markAsRead(
            @PathVariable UUID activityId,
            @RequestBody MarkChatMessagesAsReadRequest request) {

        log.info("[Chat] Marcando mensagens como lidas | activityId={} | userId={}",
                activityId, request.getUserId());

        markChatMessagesAsReadUseCase.execute(activityId, request.getUserId());

        return ResponseEntity.noContent().build();
    }

    private UUID getAuthenticatedUserId() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return UUID.fromString(userId);
    }
}

