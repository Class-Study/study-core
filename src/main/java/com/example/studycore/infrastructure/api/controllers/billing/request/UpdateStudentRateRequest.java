package com.example.studycore.infrastructure.api.controllers.billing.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record UpdateStudentRateRequest(
        @NotNull @DecimalMin(value = "0.0") BigDecimal classRate
) {
}

