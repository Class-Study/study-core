package com.example.studycore.application.usecase.billing.output;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record GetStudentBillingOutput(
        String studentName,
        LocalDate contractStartDate,
        LocalDate contractEndDate,
        BigDecimal classRate,
        String teacherName,
        String teacherEmail,
        String teacherPixKey,
        List<MonthlyBillingItemOutput> monthlyBilling
) {
    public record MonthlyBillingItemOutput(
            UUID id,
            Integer month,
            Integer year,
            String monthYear,
            Integer classCount,
            BigDecimal classValue,
            BigDecimal totalValue,
            LocalDate dueDate,
            String status
    ) {
    }
}

