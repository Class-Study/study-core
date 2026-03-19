package com.example.studycore.infrastructure.config;

import com.example.studycore.infrastructure.adapter.websocket.SignalingWebSocketHandler;
import com.example.studycore.infrastructure.adapter.websocket.WorkspaceWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final WorkspaceWebSocketHandler workspaceHandler;
    private final SignalingWebSocketHandler signalingHandler;

    public WebSocketConfig(
        WorkspaceWebSocketHandler workspaceHandler,
        SignalingWebSocketHandler signalingHandler
    ) {
        this.workspaceHandler = workspaceHandler;
        this.signalingHandler = signalingHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Rota original para workspace (sincronização de conteúdo)
        registry
                .addHandler(workspaceHandler, "/ws")
                .setAllowedOriginPatterns("*");

        // 🚀 Nova rota para sinalização WebRTC
        // Uso: ws://localhost:8080/ws/signal?workspaceId=abc-123&peerId=peer-uuid
        registry
                .addHandler(signalingHandler, "/ws/signal")
                .setAllowedOriginPatterns("*");
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(512 * 1024);
        container.setMaxBinaryMessageBufferSize(512 * 1024);
        container.setMaxSessionIdleTimeout(15 * 60 * 1000L);
        return container;
    }
}