package com.example.studycore.application.usecase.billing.output;

import java.math.BigDecimal;
import java.util.List;

public record BillingMonthOutput(
        BigDecimal totalReceived,
        BigDecimal totalPending,
        BigDecimal totalLate,
        BigDecimal totalExpected,
        int paidCount,
        int pendingCount,
        int lateCount,
        String referenceMonth,
        List<BillingRecordOutput> records
) {
}

