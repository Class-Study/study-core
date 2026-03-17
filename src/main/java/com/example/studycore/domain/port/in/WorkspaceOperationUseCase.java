package com.example.studycore.domain.port.in;

import com.example.studycore.infrastructure.adapter.websocket.WorkspaceWSMessageDTO;

public interface WorkspaceOperationUseCase {
    void handleOperation(WorkspaceWSMessageDTO message, String sessionId);
}

