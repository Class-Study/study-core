package com.example.studycore.infrastructure.api.controllers.chat.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatMessageRequest {

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("content")
    private String content;
}

