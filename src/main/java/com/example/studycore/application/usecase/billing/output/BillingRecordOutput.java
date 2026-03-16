package com.example.studycore.application.usecase.billing.output;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BillingRecordOutput(
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
        // Novos campos para detalhamento de aulas
        BigDecimal hourlyRate,
        List<String> classWeekDays,
        Integer weeksInMonth,
        Integer totalClasses,
        BigDecimal totalAmountCalculated,
        // Novo campo: data de início do aluno
        java.time.LocalDate startDate
) {
}
