package com.example.studycore.application.usecase.billing.output;

public record ConfirmPaymentOutput(
        boolean success,
        String message,
        String newStatus
) {
}

