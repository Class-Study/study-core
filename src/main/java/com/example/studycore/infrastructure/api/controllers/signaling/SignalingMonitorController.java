package com.example.studycore.infrastructure.api.controllers.signaling;

import com.example.studycore.infrastructure.adapter.websocket.service.WebRTCSignalingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para monitoramento de sinalização WebRTC.
 *
 * Endpoints:
 * - GET /api/signaling/status - Status geral
 * - GET /api/signaling/workspace/{workspaceId} - Peers em uma workspace
 */
@Slf4j
@RestController
@RequestMapping("/api/signaling")
public class SignalingMonitorController {

    private final WebRTCSignalingService signalingService;

    public SignalingMonitorController(WebRTCSignalingService signalingService) {
        this.signalingService = signalingService;
    }

    /**
     * GET /api/signaling/status
     *
     * Retorna status geral do signaling server.
     *
     * @return mapa com dados de monitoramento
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("activeWorkspaces", signalingService.getActiveWorkspacesCount());
        status.put("timestamp", System.currentTimeMillis());
        status.put("message", "WebRTC Signaling Server Running");

        log.info("📊 [Signaling] Status check | activeWorkspaces: {}",
            signalingService.getActiveWorkspacesCount());

        return ResponseEntity.ok(status);
    }

    /**
     * GET /api/signaling/workspace/{workspaceId}
     *
     * Lista os peers conectados em uma workspace específica.
     *
     * @param workspaceId identificador da workspace
     * @return lista de peers conectados
     */
    @GetMapping("/workspace/{workspaceId}")
    public ResponseEntity<Map<String, Object>> getWorkspaceStatus(
        @PathVariable String workspaceId
    ) {
        List<String> connectedPeers = signalingService.getConnectedPeersInWorkspace(workspaceId);

        Map<String, Object> response = new HashMap<>();
        response.put("workspaceId", workspaceId);
        response.put("connectedPeers", connectedPeers);
        response.put("peerCount", connectedPeers.size());
        response.put("timestamp", System.currentTimeMillis());

        log.info("📊 [Signaling] Workspace check | workspaceId: {} | peers: {}",
            workspaceId, connectedPeers.size());

        return ResponseEntity.ok(response);
    }
}

