package com.example.studycore.infrastructure.adapter.websocket;

import com.example.studycore.domain.port.out.WorkspaceSessionPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adapter que gerencia sessões WebSocket por workspace.
 *
 * Thread safety: Usa ConcurrentHashMap para rooms e sessionWorkspaceIndex.
 * Cleanup duplo: removeSession remove dos dois mapas para evitar memory leak.
 * Broadcast isolado: try/catch por sessão, falha em uma não derruba as demais.
 */
@Slf4j
@Component
public class WorkspaceSessionAdapter implements WorkspaceSessionPort {

    private final Map<String, Set<WebSocketSession>> rooms = new ConcurrentHashMap<>();
    private final Map<String, String> sessionWorkspaceIndex = new ConcurrentHashMap<>();


    @Override
    public void registerSession(String sessionId, String userId, String workspaceId, WebSocketSession session) {
        if (workspaceId == null) {
            log.warn("[WS] registerSession chamado com workspaceId nulo — sessão ignorada. sessionId={}", sessionId);
            return;
        }
        rooms.computeIfAbsent(workspaceId, k -> ConcurrentHashMap.newKeySet()).add(session);
        sessionWorkspaceIndex.put(sessionId, workspaceId);
        log.info("[WS] Sessão registrada | sessionId={}, userId={}, workspaceId={}, totalNaSala={}",
                sessionId, userId, workspaceId, rooms.get(workspaceId).size());
    }

    @Override
    public void removeSession(String sessionId) {
        String workspaceId = sessionWorkspaceIndex.remove(sessionId);
        if (workspaceId == null) {
            log.debug("[WS] removeSession: sessão não encontrada no índice | sessionId={}", sessionId);
            return;
        }
        Set<WebSocketSession> room = rooms.get(workspaceId);
        if (room != null) {
            room.removeIf(s -> s.getId().equals(sessionId));
            if (room.isEmpty()) {
                rooms.remove(workspaceId);
                log.info("[WS] Sala removida (vazia) | workspaceId={}", workspaceId);
            }
        }
        log.info("[WS] Sessão removida | sessionId={}, workspaceId={}", sessionId, workspaceId);
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
                    log.debug("[WS] Falha ao enviar mensagem para sessão {} | Erro: {}", session.getId(), e.getMessage());
                }
            }
        }

        if (enviados > 0 || falhas > 0) {
            log.debug("[WS] Broadcast concluído | workspaceId={}, enviados={}, falhas={}, payloadLength={}",
                    workspaceId, enviados, falhas, payload.length());
        }
    }
}