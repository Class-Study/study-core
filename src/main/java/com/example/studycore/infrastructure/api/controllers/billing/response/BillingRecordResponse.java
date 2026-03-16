package com.example.studycore.infrastructure.api.controllers.billing.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record BillingRecordResponse(
        UUID id,
        UUID studentId,
        String studentName,
        String levelProfileName,
        BigDecimal amount,
        BigDecimal amountAtBillingTime,
        String status,
        Instant paidAt,
        Integer notifyCount,
        String referenceMonth,
        Long daysOverdue,
        BigDecimal hourlyRate,
        List<String> classWeekDays,
        Integer weeksInMonth,
        Integer totalClasses,
        BigDecimal totalAmountCalculated,
        LocalDate startDate
) {
}
