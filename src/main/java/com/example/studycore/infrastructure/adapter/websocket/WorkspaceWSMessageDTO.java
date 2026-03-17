package com.example.studycore.infrastructure.adapter.websocket;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para mensagens WebSocket de operações em workspace.
 * Suporta operações: join, insert, delete.
 * Inclui docVersion para suportar OT (Operational Transformation) no futuro.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceWSMessageDTO {

    @JsonProperty("type")
    private String type;        // "join" | "insert" | "delete"

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("workspaceId")
    private String workspaceId;

    @JsonProperty("activityId")
    private String activityId;

    @JsonProperty("position")
    private Integer position;

    @JsonProperty("text")
    private String text;        // presente em "insert"

    @JsonProperty("length")
    private Integer length;     // presente em "delete"

    @JsonProperty("docVersion")
    private Integer docVersion;
}


