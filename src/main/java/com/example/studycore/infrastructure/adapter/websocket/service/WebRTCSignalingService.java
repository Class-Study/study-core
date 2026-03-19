package com.example.studycore.infrastructure.adapter.websocket.service;

import com.example.studycore.infrastructure.adapter.websocket.dto.SignalingMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço de sinalização WebRTC.
 *
 * Responsabilidades:
 * - Manter um mapa de sessões por workspace
 * - Rotear mensagens de sinalização entre peers
 * - NÃO persistir nada
 * - NÃO processar conteúdo
 *
 * Estrutura:
 * Map<String, Map<String, WebSocketSession>> sessionsByWorkspace
 *   workspaceId -> (peerId -> session)
 */
@Slf4j
@Service
public class WebRTCSignalingService {

    private final ObjectMapper objectMapper;

    /**
     * Map<workspaceId, Map<peerId, WebSocketSession>>
     *
     * workspaceId: identificador do workspace (ex: "abc-123")
     * peerId: identificador único do peer (ex: "student-uuid" ou "teacher-uuid")
     * WebSocketSession: sessão da conexão
     */
    private final Map<String, Map<String, WebSocketSession>> sessionsByWorkspace = new ConcurrentHashMap<>();

    public WebRTCSignalingService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * Registra uma nova sessão em um workspace.
     *
     * @param workspaceId identificador do workspace
     * @param peerId identificador único do peer
     * @param session sessão WebSocket
     */
    public void registerSession(String workspaceId, String peerId, WebSocketSession session) {
        sessionsByWorkspace
            .computeIfAbsent(workspaceId, k -> new ConcurrentHashMap<>())
            .put(peerId, session);

        log.info("✅ [WebRTC] Peer registrado | workspaceId: {} | peerId: {} | totalPeersNaWorkspace: {}",
            workspaceId, peerId, sessionsByWorkspace.get(workspaceId).size());
    }

    /**
     * Remove uma sessão de um workspace.
     *
     * @param workspaceId identificador do workspace
     * @param peerId identificador único do peer
     */
    public void removeSession(String workspaceId, String peerId) {
        Map<String, WebSocketSession> workspace = sessionsByWorkspace.get(workspaceId);
        if (workspace != null) {
            workspace.remove(peerId);
            log.info("❌ [WebRTC] Peer removido | workspaceId: {} | peerId: {} | peersRestantes: {}",
                workspaceId, peerId, workspace.size());

            // Limpar workspace se vazio
            if (workspace.isEmpty()) {
                sessionsByWorkspace.remove(workspaceId);
                log.info("🗑️ [WebRTC] Workspace vazio removido | workspaceId: {}",
                    workspaceId);
            }
        }
    }

    /**
     * Roteia uma mensagem de sinalização WebRTC para o peer de destino.
     *
     * A mensagem é encaminhada APENAS para o peer "to".
     * NÃO é reenviada para o remetente.
     * NÃO é processada ou modificada.
     *
     * @param message mensagem de sinalização
     */
    public void routeSignal(SignalingMessage message) {
        if (!message.isValid()) {
            log.warn("⚠️ [WebRTC] Mensagem de sinalização inválida: {}", message);
            return;
        }

        String workspaceId = message.workspaceId();
        String targetPeerId = message.to();

        Map<String, WebSocketSession> workspace = sessionsByWorkspace.get(workspaceId);
        if (workspace == null || workspace.isEmpty()) {
            log.warn("⚠️ [WebRTC] Workspace não encontrada ou vazia | workspaceId: {}", workspaceId);
            return;
        }

        WebSocketSession targetSession = workspace.get(targetPeerId);
        if (targetSession == null) {
            log.warn("⚠️ [WebRTC] Peer de destino não encontrado | workspaceId: {} | targetPeerId: {}",
                workspaceId, targetPeerId);
            return;
        }

        if (!targetSession.isOpen()) {
            log.warn("⚠️ [WebRTC] Sessão do peer de destino está fechada | workspaceId: {} | targetPeerId: {}",
                workspaceId, targetPeerId);
            return;
        }

        try {
            String messageJson = objectMapper.writeValueAsString(message);
            targetSession.sendMessage(new TextMessage(messageJson));
            log.debug("📨 [WebRTC] Sinal roteado | from: {} | to: {} | workspaceId: {}",
                message.from(), message.to(), workspaceId);
        } catch (IOException e) {
            log.error("❌ [WebRTC] Erro ao enviar sinal | from: {} | to: {} | workspaceId: {} | erro: {}",
                message.from(), message.to(), workspaceId, e.getMessage(), e);
        }
    }

    /**
     * Obtém informações sobre peers conectados em um workspace (apenas para debug).
     *
     * @param workspaceId identificador do workspace
     * @return lista de peerId's conectados
     */
    public List<String> getConnectedPeersInWorkspace(String workspaceId) {
        Map<String, WebSocketSession> workspace = sessionsByWorkspace.get(workspaceId);
        if (workspace == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(workspace.keySet());
    }

    /**
     * Obtém o total de workspaces ativas (apenas para monitoramento).
     *
     * @return número de workspaces ativas
     */
    public int getActiveWorkspacesCount() {
        return sessionsByWorkspace.size();
    }
}

