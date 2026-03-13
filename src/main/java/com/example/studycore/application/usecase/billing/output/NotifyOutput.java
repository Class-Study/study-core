package com.example.studycore.application.usecase.billing.output;

public record NotifyOutput(
        int notificationsSent,
        String message
) {
}

