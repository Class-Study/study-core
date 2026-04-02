package com.example.studycore.infrastructure.api.controllers.student.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record GetStudentBillingResponse(
        String studentName,
        LocalDate contractStartDate,
        LocalDate contractEndDate,
        BigDecimal classRate,
        String teacherName,
        String teacherEmail,
        String teacherPixKey,
        List<MonthlyBillingItemResponse> monthlyBilling
) {
    public record MonthlyBillingItemResponse(
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

