package com.example.studycore.infrastructure.api.controllers.chat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("activityId")
    private UUID activityId;

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("authorName")
    private String authorName;

    @JsonProperty("content")
    private String content;

    @JsonProperty("sentAt")
    private OffsetDateTime sentAt;

    @JsonProperty("readAt")
    private OffsetDateTime readAt;
}

