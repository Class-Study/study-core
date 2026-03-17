package com.example.studycore.application.usecase.exercisecollaboration;

import com.example.studycore.domain.port.in.WorkspaceOperationUseCase;
import com.example.studycore.domain.port.out.WorkspaceSessionPort;
import com.example.studycore.infrastructure.adapter.websocket.WorkspaceWSMessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

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
        if ("join".equals(message.getType())) {
            // join já tratado no afterConnectionEstablished
            // aqui pode emitir evento de presença se necessário
            return;
        }

        if ("insert".equals(message.getType()) || "delete".equals(message.getType())) {
            try {
                String payload = objectMapper.writeValueAsString(message);
                sessionPort.broadcastToWorkspace(message.getWorkspaceId(), sessionId, payload);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Erro ao serializar operação WS", e);
            }
        }
    }
}

