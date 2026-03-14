package com.example.studycore.domain.model;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.Getter;

@Getter
public class StudentNote {

    private static final Set<String> ALLOWED_TYPES = Set.of("PRIVATE", "PUBLIC");

    private final UUID id;
    private final UUID studentId;
    private final UUID authorId;
    private final String type;
    private final String content;
    private final OffsetDateTime createdAt;

    private StudentNote(
            UUID id,
            UUID studentId,
            UUID authorId,
            String type,
            String content,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.studentId = studentId;
        this.authorId = authorId;
        this.type = normalizeType(type);
        this.content = normalize(content);
        this.createdAt = createdAt;
        validate();
    }

    public static StudentNote create(UUID studentId, UUID authorId, String type, String content) {
        return new StudentNote(
                UUID.randomUUID(),
                studentId,
                authorId,
                type,
                content,
                OffsetDateTime.now()
        );
    }

    public static StudentNote with(
            UUID id,
            UUID studentId,
            UUID authorId,
            String type,
            String content,
            OffsetDateTime createdAt
    ) {
        return new StudentNote(id, studentId, authorId, type, content, createdAt);
    }

    private void validate() {
        if (id == null) {
            throw new IllegalArgumentException("Student note id cannot be null.");
        }
        if (studentId == null) {
            throw new IllegalArgumentException("Student note studentId cannot be null.");
        }
        if (authorId == null) {
            throw new IllegalArgumentException("Student note authorId cannot be null.");
        }
        if (type == null || !ALLOWED_TYPES.contains(type)) {
            throw new IllegalArgumentException("Student note type must be PRIVATE or PUBLIC.");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Student note content cannot be blank.");
        }
        if (createdAt == null) {
            throw new IllegalArgumentException("Student note createdAt cannot be null.");
        }
    }

    private static String normalize(String value) {
        return value == null ? null : value.trim();
    }

    private static String normalizeType(String value) {
        return value == null ? null : value.trim().toUpperCase();
    }
}

