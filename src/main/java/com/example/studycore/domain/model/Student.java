package com.example.studycore.domain.model;

import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.model.enums.UserStatus;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class Student {

    private final UUID id;
    private String name;
    private final String email;
    private String passwordHash;
    private final UserRole role;
    private UserStatus status;
    private String avatarUrl;
    private String phone;
    private final UUID teacherId;
    private UUID levelProfileId;
    private List<String> classDays;
    private LocalTime classTime;
    private Integer classDuration;
    private BigDecimal classRate;
    private String meetPlatform;
    private String meetLink;
    private LocalDate startDate;
    private String notesPrivate;
    private OffsetDateTime createdAt;

    private Student(
            UUID id,
            String name,
            String email,
            String passwordHash,
            UserRole role,
            UserStatus status,
            String avatarUrl,
            String phone,
            UUID teacherId,
            UUID levelProfileId,
            List<String> classDays,
            LocalTime classTime,
            Integer classDuration,
            BigDecimal classRate,
            String meetPlatform,
            String meetLink,
            LocalDate startDate,
            String notesPrivate,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
        this.avatarUrl = avatarUrl;
        this.phone = phone;
        this.teacherId = teacherId;
        this.levelProfileId = levelProfileId;
        this.classDays = classDays;
        this.classTime = classTime;
        this.classDuration = classDuration;
        this.classRate = classRate;
        this.meetPlatform = meetPlatform;
        this.meetLink = meetLink;
        this.startDate = startDate;
        this.notesPrivate = notesPrivate;
        this.createdAt = createdAt;

        validate();
    }

    public static Student create(
            String name,
            String email,
            String passwordHash,
            String avatarUrl,
            String phone,
            UUID teacherId,
            UUID levelProfileId,
            List<String> classDays,
            LocalTime classTime,
            Integer classDuration,
            BigDecimal classRate,
            String meetPlatform,
            String meetLink
    ) {
        final var id = UUID.randomUUID();
        final var createdAt = OffsetDateTime.now();
        final var startDate = LocalDate.now();

        return new Student(
                id,
                normalize(name),
                normalize(email).toLowerCase(),
                passwordHash,
                UserRole.STUDENT,
                UserStatus.ACTIVE,
                avatarUrl,
                normalizePhone(phone),
                teacherId,
                levelProfileId,
                classDays,
                classTime,
                classDuration,
                classRate,
                meetPlatform,
                meetLink,
                startDate,
                null,
                createdAt
        );
    }

    public static Student with(
            UUID id,
            String name,
            String email,
            String passwordHash,
            UserRole role,
            UserStatus status,
            String avatarUrl,
            String phone,
            UUID teacherId,
            UUID levelProfileId,
            List<String> classDays,
            LocalTime classTime,
            Integer classDuration,
            BigDecimal classRate,
            String meetPlatform,
            String meetLink,
            LocalDate startDate,
            String notesPrivate,
            OffsetDateTime createdAt
    ) {
        return new Student(
                id,
                name,
                email,
                passwordHash,
                role,
                status,
                avatarUrl,
                phone,
                teacherId,
                levelProfileId,
                classDays,
                classTime,
                classDuration,
                classRate,
                meetPlatform,
                meetLink,
                startDate,
                notesPrivate,
                createdAt
        );
    }

    private void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be null or blank");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
    }

    private static String normalize(String s) {
        return s.trim();
    }

    private static String normalizePhone(String p) {
        if (p == null) return null;
        return p.replaceAll("\\D", "");
    }
}

