package com.example.studycore.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
public class StudentBilling {

    private static final Set<String> ALLOWED_STATUS = Set.of("PENDING", "OVERDUE", "PAID", "AWAITING_CONFIRMATION");

    private final UUID id;
    private final UUID studentId;
    private final Integer month;
    private final Integer year;
    private final Integer classCount;
    private final BigDecimal classValue;
    private final BigDecimal totalValue;
    private final LocalDate dueDate;
    private String status;
    private final OffsetDateTime createdAt;
    private OffsetDateTime paidAt;

    private StudentBilling(
            UUID id,
            UUID studentId,
            Integer month,
            Integer year,
            Integer classCount,
            BigDecimal classValue,
            BigDecimal totalValue,
            LocalDate dueDate,
            String status,
            OffsetDateTime createdAt,
            OffsetDateTime paidAt
    ) {
        this.id = id;
        this.studentId = studentId;
        this.month = month;
        this.year = year;
        this.classCount = classCount;
        this.classValue = classValue;
        this.totalValue = totalValue;
        this.dueDate = dueDate;
        this.status = normalizeStatus(status);
        this.createdAt = createdAt;
        this.paidAt = paidAt;
        validate();
    }

    public static StudentBilling create(
            UUID studentId,
            Integer month,
            Integer year,
            Integer classCount,
            BigDecimal classValue,
            BigDecimal totalValue,
            LocalDate dueDate
    ) {
        return new StudentBilling(
                UUID.randomUUID(),
                studentId,
                month,
                year,
                classCount,
                classValue,
                totalValue,
                dueDate,
                "PENDING",
                OffsetDateTime.now(),
                null
        );
    }

    public static StudentBilling with(
            UUID id,
            UUID studentId,
            Integer month,
            Integer year,
            Integer classCount,
            BigDecimal classValue,
            BigDecimal totalValue,
            LocalDate dueDate,
            String status,
            OffsetDateTime createdAt,
            OffsetDateTime paidAt
    ) {
        return new StudentBilling(id, studentId, month, year, classCount, classValue, totalValue,
                dueDate, status, createdAt, paidAt);
    }

    public StudentBilling markAsPaid() {
        return new StudentBilling(this.id, this.studentId, this.month, this.year,
                this.classCount, this.classValue, this.totalValue, this.dueDate,
                "PAID", this.createdAt, OffsetDateTime.now());
    }

    public StudentBilling markAsAwaitingConfirmation() {
        return new StudentBilling(this.id, this.studentId, this.month, this.year,
                this.classCount, this.classValue, this.totalValue, this.dueDate,
                "AWAITING_CONFIRMATION", this.createdAt, this.paidAt);
    }

    private void validate() {
        if (id == null) throw new IllegalArgumentException("StudentBilling id cannot be null.");
        if (studentId == null) throw new IllegalArgumentException("StudentBilling studentId cannot be null.");
        if (month == null || month < 1 || month > 12) throw new IllegalArgumentException("StudentBilling month must be between 1 and 12.");
        if (year == null || year < 2000) throw new IllegalArgumentException("StudentBilling year is invalid.");
        if (classCount == null || classCount < 0) throw new IllegalArgumentException("StudentBilling classCount must be zero or greater.");
        if (classValue == null || classValue.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("StudentBilling classValue must be zero or greater.");
        if (totalValue == null || totalValue.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("StudentBilling totalValue must be zero or greater.");
        if (dueDate == null) throw new IllegalArgumentException("StudentBilling dueDate cannot be null.");
        if (status == null || !ALLOWED_STATUS.contains(status)) throw new IllegalArgumentException("StudentBilling status is invalid.");
        if (createdAt == null) throw new IllegalArgumentException("StudentBilling createdAt cannot be null.");
    }

    private static String normalizeStatus(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }
}

