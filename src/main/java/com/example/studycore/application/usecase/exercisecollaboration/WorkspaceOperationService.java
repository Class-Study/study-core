package com.example.studycore.application.usecase.exercisecollaboration;

import com.example.studycore.domain.port.in.WorkspaceOperationUseCase;
import com.example.studycore.domain.port.out.WorkspaceSessionPort;
import com.example.studycore.infrastructure.adapter.websocket.WorkspaceWSMessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WorkspaceOperationService implements WorkspaceOperationUseCase {

    private final WorkspaceSessionPort sessionPort;
    private final ObjectMapper objectMapper;

    public WorkspaceOperationService(WorkspaceSessionPort sessionPort, ObjectMapper objectMapper) {
        this.sessionPort = sessionPort;
        this.objectMapper = objectMapper;
    }

    @Override
    public void handleOperation(WorkspaceWSMessageDTO message, String sessionId) {
        if (message == null || message.getType() == null) {
            log.warn("[WS] Mensagem nula ou sem tipo recebida");
            return;
        }

        switch (message.getType()) {
            case "join" -> handleJoin(message);
            case "sync" -> handleSync(message, sessionId);
            case "yjs-update" -> handleYjsUpdate(message, sessionId);
            case "insert", "delete" -> handleInsertDelete(message, sessionId);
            case "cursor" -> handleCursor(message, sessionId);
            default -> log.warn("[WS] Tipo de mensagem desconhecido: {}", message.getType());
        }
    }

    private void handleJoin(WorkspaceWSMessageDTO message) {
        log.info("[WS] join recebido. userId={}, workspaceId={}",
            message.getUserId(), message.getWorkspaceId());
    }

    private void handleSync(WorkspaceWSMessageDTO message, String sessionId) {
        int htmlLength = message.getHtml() != null ? message.getHtml().length() : 0;
        log.info("[WS] sync recebido. activityId={}, htmlLength={}",
            message.getActivityId(), htmlLength);
        broadcast(message, sessionId);
    }

    private void handleYjsUpdate(WorkspaceWSMessageDTO message, String sessionId) {
        if (message.getActivityId() == null || message.getUpdate() == null) {
            log.warn("[WS] yjs-update inválido recebido | activityId={}, hasUpdate={}",
                message.getActivityId(), message.getUpdate() != null);
            return;
        }

        log.debug("[WS] yjs-update recebido | activityId={}, updateSize={}bytes",
            message.getActivityId(), message.getUpdate().length());

        broadcast(message, sessionId);
    }

    private void handleInsertDelete(WorkspaceWSMessageDTO message, String sessionId) {
        log.info("[WS] {} recebido. activityId={}, position={}, docVersion={}",
            message.getType(), message.getActivityId(),
            message.getPosition(), message.getDocVersion());
        broadcast(message, sessionId);
    }

    private void handleCursor(WorkspaceWSMessageDTO message, String sessionId) {
        log.debug("[WS] cursor recebido. userId={}, from={}, to={}, userName={}",
            message.getUserId(), message.getFrom(), message.getTo(), message.getUserName());
        broadcast(message, sessionId);
    }

    private void broadcast(WorkspaceWSMessageDTO message, String sessionId) {
        try {
            String payload = objectMapper.writeValueAsString(message);
            sessionPort.broadcastToWorkspace(message.getWorkspaceId(), sessionId, payload);
        } catch (JsonProcessingException e) {
            log.error("[WS] Erro ao serializar mensagem tipo={}", message.getType(), e);
        }
    }
}
