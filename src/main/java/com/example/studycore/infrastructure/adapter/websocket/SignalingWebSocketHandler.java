package com.example.studycore.infrastructure.adapter.websocket;

import com.example.studycore.infrastructure.adapter.websocket.dto.SignalingMessage;
import com.example.studycore.infrastructure.adapter.websocket.service.WebRTCSignalingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Handler WebSocket para sinalização WebRTC.
 *
 * Fluxo:
 * 1. afterConnectionEstablished: registra o peer no workspace
 * 2. handleTextMessage: roteia a mensagem de sinalização para o peer de destino
 * 3. afterConnectionClosed: remove o peer do workspace
 *
 * 🔑 IMPORTANTE:
 * - NÃO processa conteúdo
 * - NÃO persiste em banco
 * - NÃO faz lógica de negócio
 * - APENAS roteia sinais WebRTC (offer, answer, ICE)
 */
@Slf4j
@Component
public class SignalingWebSocketHandler extends TextWebSocketHandler {

    private final WebRTCSignalingService signalingService;
    private final ObjectMapper objectMapper;

    public SignalingWebSocketHandler(
        WebRTCSignalingService signalingService,
        ObjectMapper objectMapper
    ) {
        this.signalingService = signalingService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            String workspaceId = getQueryParam(session, "workspaceId");
            String peerId = getQueryParam(session, "peerId");

            if (workspaceId == null || workspaceId.isBlank() || peerId == null || peerId.isBlank()) {
                log.warn("⚠️ [Signaling] Conexão recusada: workspaceId ou peerId ausente | sessionId: {}",
                    session.getId());
                session.close(CloseStatus.BAD_DATA);
                return;
            }

            signalingService.registerSession(workspaceId, peerId, session);

            log.info("✅ [Signaling] Conexão estabelecida | sessionId: {} | workspaceId: {} | peerId: {}",
                session.getId(), workspaceId, peerId);
        } catch (Exception e) {
            log.error("❌ [Signaling] Erro ao estabelecer conexão: {}", e.getMessage(), e);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            SignalingMessage signalingMessage = objectMapper.readValue(
                message.getPayload(),
                SignalingMessage.class
            );

            log.debug("📨 [Signaling] Mensagem recebida | from: {} | to: {} | workspaceId: {}",
                signalingMessage.from(), signalingMessage.to(), signalingMessage.workspaceId());

            signalingService.routeSignal(signalingMessage);
        } catch (Exception e) {
            log.error("❌ [Signaling] Erro ao processar mensagem: {}", e.getMessage(), e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        try {
            String workspaceId = getQueryParam(session, "workspaceId");
            String peerId = getQueryParam(session, "peerId");

            if (workspaceId != null && peerId != null) {
                signalingService.removeSession(workspaceId, peerId);
                log.info("🔴 [Signaling] Conexão fechada | sessionId: {} | workspaceId: {} | peerId: {} | closeCode: {}",
                    session.getId(), workspaceId, peerId, status.getCode());
            }
        } catch (Exception e) {
            log.error("❌ [Signaling] Erro ao fechar conexão: {}", e.getMessage(), e);
        }
    }

    /**
     * Extrai um parâmetro da query string da URI WebSocket.
     *
     * @param session sessão WebSocket
     * @param param nome do parâmetro
     * @return valor do parâmetro ou null
     */
    private String getQueryParam(WebSocketSession session, String param) {
        try {
            if (session.getUri() == null) {
                return null;
            }
            String query = session.getUri().getQuery();
            if (query == null) {
                return null;
            }
            for (String part : query.split("&")) {
                String[] kv = part.split("=", 2);
                if (kv.length == 2 && kv[0].equals(param)) {
                    return java.net.URLDecoder.decode(kv[1], java.nio.charset.StandardCharsets.UTF_8);
                }
            }
        } catch (Exception e) {
            log.warn("⚠️ [Signaling] Erro ao extrair parâmetro {} da query string: {}",
                param, e.getMessage());
        }
        return null;
    }
}


