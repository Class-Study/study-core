package com.example.studycore.infrastructure.api.controllers.billing.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BillingRecordResponse(
        UUID id,
        UUID studentId,
        String studentName,
        String levelProfileName,
        BigDecimal amount,
        String status,
        Instant paidAt,
        Integer notifyCount,
        String referenceMonth
) {
}

