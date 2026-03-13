package com.example.studycore.infrastructure.api.controllers.billing.response;

import java.math.BigDecimal;
import java.util.List;

public record BillingMonthResponse(
        BigDecimal totalReceived,
        BigDecimal totalPending,
        BigDecimal totalLate,
        BigDecimal totalExpected,
        int paidCount,
        int pendingCount,
        int lateCount,
        String referenceMonth,
        List<BillingRecordResponse> records
) {
}

