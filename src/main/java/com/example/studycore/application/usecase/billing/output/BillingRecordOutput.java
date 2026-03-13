package com.example.studycore.application.usecase.billing.output;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record BillingRecordOutput(
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

