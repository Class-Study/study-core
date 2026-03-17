package com.example.studycore.infrastructure.adapter.websocket;

import com.example.studycore.domain.port.in.WorkspaceOperationUseCase;
import com.example.studycore.domain.port.out.WorkspaceSessionPort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.logging.Logger;

@Component
public class WorkspaceWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = Logger.getLogger(WorkspaceWebSocketHandler.class.getName());

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

//        logger.info("🟢 WebSocket connection established:");
//        logger.info("   - sessionId: " + session.getId());
//        logger.info("   - userId: " + userId);
//        logger.info("   - workspaceId: " + workspaceId);
//        logger.info("   - uri: " + session.getUri());
//        logger.info("   - remoteAddress: " + session.getRemoteAddress());

        sessionPort.registerSession(session.getId(), userId, workspaceId, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
//            logger.info("📨 Message received from " + session.getId() + ": " + message.getPayload());
            WorkspaceWSMessageDTO dto = objectMapper.readValue(message.getPayload(), WorkspaceWSMessageDTO.class);
//            logger.info("   - Type: " + dto.getType() + ", Workspace: " + dto.getWorkspaceId());
            operationUseCase.handleOperation(dto, session.getId());
        } catch (Exception e) {
            logger.severe("❌ Erro ao processar mensagem WebSocket: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
//        logger.info("🔴 WebSocket connection closed:");
//        logger.info("   - sessionId: " + session.getId());
//        logger.info("   - closeStatus: " + status.getCode() + " " + status.getReason());
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


