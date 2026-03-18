package com.example.studycore.infrastructure.adapter.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para mensagens WebSocket de operações em workspace.
 *
 * Tipos de mensagem suportados:
 * - join: registro de sessão
 * - sync: sincronização completa de conteúdo (compatibilidade)
 * - yjs-update: atualização incremental CRDT (Yjs) - NOVO
 * - insert, delete, cursor: operações legadas (mantidas por compatibilidade)
 *
 * NOVO PROTOCOLO (Yjs):
 * O campo 'update' contém um Uint8Array codificado em Base64.
 * O backend atua como relay puro, sem interpretar o conteúdo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkspaceWSMessageDTO {

    @JsonProperty("type")
    private String type;        // "join" | "sync" | "yjs-update" | "insert" | "delete" | "cursor"

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("workspaceId")
    private String workspaceId;

    @JsonProperty("activityId")
    private String activityId;

    // ===== Operações Legadas (insert/delete/cursor) =====
    @JsonProperty("position")
    private Integer position;

    @JsonProperty("text")
    private String text;

    @JsonProperty("length")
    private Integer length;

    @JsonProperty("docVersion")
    private Integer docVersion;

    // ===== Sync (compatibilidade) =====
    @JsonProperty("html")
    private String html;

    // ===== Cursor (legado) =====
    @JsonProperty("from")
    private Integer from;

    @JsonProperty("to")
    private Integer to;

    @JsonProperty("userName")
    private String userName;

    // ===== Yjs CRDT (NOVO) =====
    /**
     * Update incremental do Yjs codificado em Base64.
     * Contém um Uint8Array que o frontend decodifica.
     * Backend NÃO DEVE interpretar este conteúdo.
     */
    @JsonProperty("update")
    private String update;
}


