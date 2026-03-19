package com.example.studycore.application.usecase.exercisecollaboration;

import com.example.studycore.domain.port.in.WorkspaceOperationUseCase;
import com.example.studycore.domain.port.out.WorkspaceSessionPort;
import com.example.studycore.domain.port.out.SnapshotPersistencePort;
import com.example.studycore.infrastructure.adapter.websocket.WorkspaceWSMessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WorkspaceOperationService implements WorkspaceOperationUseCase {

    private final WorkspaceSessionPort sessionPort;
    private final SnapshotPersistencePort snapshotPersistencePort;
    private final ObjectMapper objectMapper;

    public WorkspaceOperationService(
        WorkspaceSessionPort sessionPort,
        SnapshotPersistencePort snapshotPersistencePort,
        ObjectMapper objectMapper
    ) {
        this.sessionPort = sessionPort;
        this.snapshotPersistencePort = snapshotPersistencePort;
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
            case "snapshot" -> handleSnapshot(message, sessionId);
            case "cursor" -> handleCursor(message, sessionId);
            default -> log.warn("[WS] Tipo de mensagem desconhecido: {}", message.getType());
        }
    }

    private void handleJoin(WorkspaceWSMessageDTO message) {
        log.info("[WS] join recebido. userId={}, workspaceId={}",
            message.getUserId(), message.getWorkspaceId());
    }

    private void handleSnapshot(WorkspaceWSMessageDTO message, String sessionId) {
        if (message.getActivityId() == null || message.getSnapshot() == null) {
            log.warn("[WS] snapshot inválido recebido | activityId={}", message.getActivityId());
            return;
        }

        log.debug("[WS] snapshot recebido | activityId={} | payloadSize={}",
            message.getActivityId(), message.getSnapshot().length());

        try {
            java.util.UUID activityId = java.util.UUID.fromString(message.getActivityId());
            // Persiste o snapshot (base64 gzip) recebido
            snapshotPersistencePort.saveSnapshot(activityId, message.getSnapshot());
        } catch (IllegalArgumentException e) {
            log.warn("[WS] activityId inválido para snapshot | activityId={}", message.getActivityId());
        }

        // Faz broadcast para todas as sessões exceto o remetente
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


