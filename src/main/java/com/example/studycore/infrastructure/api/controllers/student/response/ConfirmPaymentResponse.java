package com.example.studycore.infrastructure.api.controllers.student.response;

public record ConfirmPaymentResponse(
        boolean success,
        String message,
        String newStatus
) {
}

