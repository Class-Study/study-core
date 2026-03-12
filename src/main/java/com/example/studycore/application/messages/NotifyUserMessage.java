package com.example.studycore.application.messages;

import java.io.Serializable;

public record NotifyUserMessage(
        String email,
        String title,
        String body
) implements Serializable {
}
