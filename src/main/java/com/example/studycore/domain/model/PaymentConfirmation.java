package com.example.studycore.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class PaymentConfirmation {

    private final UUID id;
    private final UUID billingId;
    private final String paymentMethod;
    private final String pixKey;
    private final BigDecimal amount;
    private final boolean confirmedByTeacher;
    private final OffsetDateTime createdAt;

    private PaymentConfirmation(
            UUID id,
            UUID billingId,
            String paymentMethod,
            String pixKey,
            BigDecimal amount,
            boolean confirmedByTeacher,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.billingId = billingId;
        this.paymentMethod = paymentMethod;
        this.pixKey = pixKey;
        this.amount = amount;
        this.confirmedByTeacher = confirmedByTeacher;
        this.createdAt = createdAt;
        validate();
    }

    public static PaymentConfirmation create(
            UUID billingId,
            String paymentMethod,
            String pixKey,
            BigDecimal amount
    ) {
        return new PaymentConfirmation(
                UUID.randomUUID(),
                billingId,
                paymentMethod,
                pixKey,
                amount,
                false,
                OffsetDateTime.now()
        );
    }

    public static PaymentConfirmation with(
            UUID id,
            UUID billingId,
            String paymentMethod,
            String pixKey,
            BigDecimal amount,
            boolean confirmedByTeacher,
            OffsetDateTime createdAt
    ) {
        return new PaymentConfirmation(id, billingId, paymentMethod, pixKey, amount, confirmedByTeacher, createdAt);
    }

    private void validate() {
        if (id == null) throw new IllegalArgumentException("PaymentConfirmation id cannot be null.");
        if (billingId == null) throw new IllegalArgumentException("PaymentConfirmation billingId cannot be null.");
        if (paymentMethod == null || paymentMethod.isBlank()) throw new IllegalArgumentException("PaymentConfirmation paymentMethod cannot be blank.");
        if (pixKey == null || pixKey.isBlank()) throw new IllegalArgumentException("PaymentConfirmation pixKey cannot be blank.");
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("PaymentConfirmation amount must be greater than zero.");
        if (createdAt == null) throw new IllegalArgumentException("PaymentConfirmation createdAt cannot be null.");
    }
}

