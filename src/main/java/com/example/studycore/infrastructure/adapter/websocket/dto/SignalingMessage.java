package com.example.studycore.infrastructure.adapter.websocket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para mensagens de sinalização WebRTC.
 *
 * Estrutura:
 * {
 *   "type": "signal",
 *   "workspaceId": "abc-123",
 *   "from": "peer-id-1",
 *   "to": "peer-id-2",
 *   "signal": { ... } // SDP offer/answer ou ICE candidate
 * }
 */
public record SignalingMessage(
    @JsonProperty("type") String type,
    @JsonProperty("workspaceId") String workspaceId,
    @JsonProperty("from") String from,
    @JsonProperty("to") String to,
    @JsonProperty("signal") Object signal
) {
    public boolean isValid() {
        return "signal".equals(type)
            && workspaceId != null && !workspaceId.isBlank()
            && from != null && !from.isBlank()
            && to != null && !to.isBlank()
            && signal != null;
    }
}


