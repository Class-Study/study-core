package com.example.studycore.domain.port.out;

import org.springframework.web.socket.WebSocketSession;

public interface WorkspaceSessionPort {
    void registerSession(String sessionId, String userId, String workspaceId, WebSocketSession session);

    void removeSession(String sessionId);

    void broadcastToWorkspace(String workspaceId, String excludeSessionId, String payload);
}

