package com.example.studycore.infrastructure.adapter.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WorkspaceWSMessageDTO {

    @JsonProperty("type")
    private String type;        // "join" | "snapshot" | "cursor"

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("workspaceId")
    private String workspaceId;

    @JsonProperty("activityId")
    private String activityId;

    @JsonProperty("snapshot")
    private String snapshot;    // base64 gzip do HTML completo

    @JsonProperty("from")
    private Integer from;

    @JsonProperty("to")
    private Integer to;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("data")
    private Object data;

    @JsonProperty("role")
    private String role;

    @JsonProperty("activityTitle")
    private String activityTitle;
}
