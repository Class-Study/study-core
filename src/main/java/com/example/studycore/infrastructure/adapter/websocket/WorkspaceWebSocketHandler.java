package com.example.studycore.infrastructure.adapter.websocket;

import com.example.studycore.domain.port.in.WorkspaceOperationUseCase;
import com.example.studycore.domain.port.out.WorkspaceSessionPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 * Handler que processa conexões WebSocket puras (sem STOMP, sem SockJS).
 *
 * Fluxo:
 * 1. afterConnectionEstablished: extrai userId e workspaceId da query string
 * 2. handleTextMessage: deserializa JSON e delega para o UseCase
 * 3. afterConnectionClosed: remove sessão dos mapas
 */
@Slf4j
@Component
public class WorkspaceWebSocketHandler extends TextWebSocketHandler {

    private final WorkspaceOperationUseCase operationUseCase;
    private final WorkspaceSessionPort sessionPort;
    private final ObjectMapper objectMapper;

    public WorkspaceWebSocketHandler(
        WorkspaceOperationUseCase operationUseCase,
        WorkspaceSessionPort sessionPort,
        ObjectMapper objectMapper
    ) {
        this.operationUseCase = operationUseCase;
        this.sessionPort = sessionPort;
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = getQueryParam(session, "userId");
        String workspaceId = getQueryParam(session, "workspaceId");

        log.info("🟢 [WS] Conexão estabelecida | sessionId: {} | userId: {} | workspaceId: {} | uri: {}",
            session.getId(), userId, workspaceId, session.getUri());

        sessionPort.registerSession(session.getId(), userId, workspaceId, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            WorkspaceWSMessageDTO dto = objectMapper.readValue(message.getPayload(), WorkspaceWSMessageDTO.class);

            // Log detalhado apenas para tipos que não são cursor (alto volume)
            if (!"cursor".equals(dto.getType())) {
                log.debug("📨 [WS] Mensagem recebida | type: {} | sessionId: {} | payload: {}",
                    dto.getType(), session.getId(), message.getPayload());
            }

            operationUseCase.handleOperation(dto, session.getId());
        } catch (Exception e) {
            log.error("❌ [WS] Erro ao processar mensagem WebSocket: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info("🔴 [WS] Conexão fechada | sessionId: {} | closeCode: {} | reason: {}",
            session.getId(), status.getCode(), status.getReason());
        sessionPort.removeSession(session.getId());
    }

    private String getQueryParam(WebSocketSession session, String param) {
        if (session.getUri() == null) {
            return null;
        }
        String query = session.getUri().getQuery();
        if (query == null) {
            return null;
        }
        for (String part : query.split("&")) {
            String[] kv = part.split("=");
            if (kv.length == 2 && kv[0].equals(param)) {
                return kv[1];
            }
        }
        return null;
    }
}


