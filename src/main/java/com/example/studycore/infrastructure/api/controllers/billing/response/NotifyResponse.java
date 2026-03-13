package com.example.studycore.infrastructure.api.controllers.billing.response;

public record NotifyResponse(
        int notificationsSent,
        String message
) {
}

