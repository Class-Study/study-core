package com.example.studycore.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
public class BillingRecord {

    private static final Set<String> ALLOWED_STATUS = Set.of("PAID", "PENDING", "OVERDUE");

    private final UUID id;
    private final UUID studentId;
    private final LocalDate referenceMonth;
    private final LocalDate dueDate;
    private final BigDecimal amount;
    private final BigDecimal amountAtBillingTime;
    private final String status;
    private final OffsetDateTime paidAt;
    private final OffsetDateTime notifiedAt;
    private final Integer notifyCount;
    private final String notes;
    private final OffsetDateTime createdAt;

    private BillingRecord(
            UUID id,
            UUID studentId,
            LocalDate referenceMonth,
            LocalDate dueDate,
            BigDecimal amount,
            BigDecimal amountAtBillingTime,
            String status,
            OffsetDateTime paidAt,
            OffsetDateTime notifiedAt,
            Integer notifyCount,
            String notes,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.studentId = studentId;
        this.referenceMonth = referenceMonth;
        this.dueDate = dueDate;
        this.amount = amount;
        this.amountAtBillingTime = amountAtBillingTime;
        this.status = normalizeStatus(status);
        this.paidAt = paidAt;
        this.notifiedAt = notifiedAt;
        this.notifyCount = notifyCount == null ? 0 : notifyCount;
        this.notes = normalizeNullable(notes);
        this.createdAt = createdAt;
        validate();
    }

    public static BillingRecord create(
            UUID studentId,
            LocalDate referenceMonth,
            LocalDate dueDate,
            BigDecimal amount,
            String notes
    ) {
        return new BillingRecord(
                UUID.randomUUID(),
                studentId,
                referenceMonth,
                dueDate,
                amount,
                amount, // amountAtBillingTime = snapshot do amount no momento da criação
                "PENDING",
                null,
                null,
                0,
                notes,
                OffsetDateTime.now()
        );
    }

    public static BillingRecord with(
            UUID id,
            UUID studentId,
            LocalDate referenceMonth,
            LocalDate dueDate,
            BigDecimal amount,
            BigDecimal amountAtBillingTime,
            String status,
            OffsetDateTime paidAt,
            OffsetDateTime notifiedAt,
            Integer notifyCount,
            String notes,
            OffsetDateTime createdAt
    ) {
        return new BillingRecord(
                id,
                studentId,
                referenceMonth,
                dueDate,
                amount,
                amountAtBillingTime,
                status,
                paidAt,
                notifiedAt,
                notifyCount,
                notes,
                createdAt
        );
    }

    public BillingRecord markAsPaid() {
        return new BillingRecord(
                this.id,
                this.studentId,
                this.referenceMonth,
                this.dueDate,
                this.amount,
                this.amountAtBillingTime,
                "PAID",
                OffsetDateTime.now(),
                this.notifiedAt,
                this.notifyCount,
                this.notes,
                this.createdAt
        );
    }

    public Long getDaysOverdue() {
        if (this.paidAt != null || !this.status.equals("OVERDUE")) {
            return null;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(this.dueDate, LocalDate.now());
    }

    private void validate() {
        if (id == null) {
            throw new IllegalArgumentException("BillingRecord id cannot be null.");
        }
        if (studentId == null) {
            throw new IllegalArgumentException("BillingRecord studentId cannot be null.");
        }
        if (referenceMonth == null) {
            throw new IllegalArgumentException("BillingRecord referenceMonth cannot be null.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("BillingRecord amount must be zero or greater.");
        }
        if (status == null || !ALLOWED_STATUS.contains(status)) {
            throw new IllegalArgumentException("BillingRecord status must be PAID, PENDING or LATE.");
        }
        if (notifyCount == null || notifyCount < 0) {
            throw new IllegalArgumentException("BillingRecord notifyCount must be zero or greater.");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("BillingRecord createdAt cannot be null.");
        }
    }

    private static String normalizeStatus(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }

    private static String normalizeNullable(String value) {
        if (value == null) return null;
        final var normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
