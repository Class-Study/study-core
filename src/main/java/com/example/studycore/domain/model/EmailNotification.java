package com.example.studycore.domain.model;

import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
public class EmailNotification {

    private static final Set<String> ALLOWED_STATUS = Set.of("SENT", "DELIVERED", "FAILED", "OPENED");

    private final UUID id;
    private final UUID billingRecordId;
    private final String recipientEmail;
    private final String subject;
    private final String status;
    private final OffsetDateTime sentAt;
    private final String providerId;

    private EmailNotification(
            UUID id,
            UUID billingRecordId,
            String recipientEmail,
            String subject,
            String status,
            OffsetDateTime sentAt,
            String providerId
    ) {
        this.id = id;
        this.billingRecordId = billingRecordId;
        this.recipientEmail = normalizeEmail(recipientEmail);
        this.subject = normalize(subject);
        this.status = normalizeStatus(status);
        this.sentAt = sentAt;
        this.providerId = normalizeNullable(providerId);
        validate();
    }

    public static EmailNotification create(
            UUID billingRecordId,
            String recipientEmail,
            String subject,
            String status,
            String providerId
    ) {
        return new EmailNotification(
                UUID.randomUUID(),
                billingRecordId,
                recipientEmail,
                subject,
                status,
                OffsetDateTime.now(),
                providerId
        );
    }

    public static EmailNotification with(
            UUID id,
            UUID billingRecordId,
            String recipientEmail,
            String subject,
            String status,
            OffsetDateTime sentAt,
            String providerId
    ) {
        return new EmailNotification(id, billingRecordId, recipientEmail, subject, status, sentAt, providerId);
    }

    private void validate() {
        if (id == null) {
            throw new IllegalArgumentException("EmailNotification id cannot be null.");
        }
        if (billingRecordId == null) {
            throw new IllegalArgumentException("EmailNotification billingRecordId cannot be null.");
        }
        if (recipientEmail == null || recipientEmail.isBlank()) {
            throw new IllegalArgumentException("EmailNotification recipientEmail cannot be blank.");
        }
        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException("EmailNotification subject cannot be blank.");
        }
        if (status == null || !ALLOWED_STATUS.contains(status)) {
            throw new IllegalArgumentException("EmailNotification status must be SENT, DELIVERED, FAILED or OPENED.");
        }
        if (sentAt == null) {
            throw new IllegalArgumentException("EmailNotification sentAt cannot be null.");
        }
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private static String normalizeEmail(String value) {
        if (value == null) return null;
        return value.trim().toLowerCase();
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
