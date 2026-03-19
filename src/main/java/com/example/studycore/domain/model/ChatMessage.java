package com.example.studycore.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private UUID id;
    private UUID activityId;
    private UUID userId;
    private String content;
    private OffsetDateTime sentAt;
    private OffsetDateTime readAt;
    private String authorName;  // Preenchido via JOIN com users
}



