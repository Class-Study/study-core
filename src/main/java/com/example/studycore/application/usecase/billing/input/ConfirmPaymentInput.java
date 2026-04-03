package com.example.studycore.application.usecase.billing.input;

import java.math.BigDecimal;
import java.util.UUID;

public record ConfirmPaymentInput(
        UUID studentId,
        UUID billingId,
        String paymentMethod,
        String pixKey,
        BigDecimal amount
) {
}

