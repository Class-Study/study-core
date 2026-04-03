package com.example.studycore.infrastructure.api.controllers.student.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ConfirmPaymentRequest(

        @NotNull(message = "billingId é obrigatório")
        UUID billingId,

        @NotBlank(message = "paymentMethod é obrigatório")
        String paymentMethod,

        @NotBlank(message = "pixKey é obrigatória")
        String pixKey,

        @NotNull(message = "amount é obrigatório")
        @DecimalMin(value = "0.01", message = "amount deve ser maior que zero")
        BigDecimal amount
)
{}

