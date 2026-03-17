package com.example.studycore.infrastructure.adapter.websocket;

import com.example.studycore.domain.port.out.WorkspaceSessionPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WorkspaceSessionAdapter implements WorkspaceSessionPort {

    private static final Logger log = LoggerFactory.getLogger(WorkspaceSessionAdapter.class);

    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private final Map<String, String> sessionWorkspaceIndex = new ConcurrentHashMap<>();

    public WorkspaceSessionAdapter() {
        log.info("[WS] WorkspaceSessionAdapter inicializado");
    }

    @Override
    public void registerSession(String sessionId, String userId, String workspaceId, WebSocketSession session) {
        if (workspaceId == null) {
            log.warn("[WS] registerSession chamado com workspaceId nulo — sessão ignorada. sessionId={}", sessionId);
            return;
        }
        rooms.computeIfAbsent(workspaceId, k -> ConcurrentHashMap.newKeySet()).add(session);
        sessionWorkspaceIndex.put(sessionId, workspaceId);
        log.info("[WS] Sessão registrada. sessionId={}, userId={}, workspaceId={}, totalNaSala={}",
                sessionId, userId, workspaceId, rooms.get(workspaceId).size());
    }

    @Override
    public void removeSession(String sessionId) {
        String workspaceId = sessionWorkspaceIndex.remove(sessionId);
        if (workspaceId == null) {
            log.warn("[WS] removeSession: sessão não encontrada no índice. sessionId={}", sessionId);
            return;
        }
        Set<WebSocketSession> room = rooms.get(workspaceId);
        if (room != null) {
            room.removeIf(s -> s.getId().equals(sessionId));
            if (room.isEmpty()) {
                rooms.remove(workspaceId);
                log.info("[WS] Sala removida (vazia). workspaceId={}", workspaceId);
            }
        }
        log.info("[WS] Sessão removida. sessionId={}, workspaceId={}", sessionId, workspaceId);
    }

    @Override
    public void broadcastToWorkspace(String workspaceId, String excludeSessionId, String payload) {
        Set<WebSocketSession> room = rooms.getOrDefault(workspaceId, Set.of());
        int enviados = 0;
        int falhas = 0;

        for (WebSocketSession session : room) {
            if (session.isOpen() && !session.getId().equals(excludeSessionId)) {
                try {
                    session.sendMessage(new TextMessage(payload));
                    enviados++;
                } catch (IOException e) {
                    falhas++;
                    log.error("[WS] Falha ao enviar mensagem para sessão {}. Erro: {}", session.getId(), e.getMessage());
                }
            }
        }

        log.info("[WS] Broadcast concluído. workspaceId={}, enviados={}, falhas={}, payload={}",
                workspaceId, enviados, falhas, payload);
    }
}