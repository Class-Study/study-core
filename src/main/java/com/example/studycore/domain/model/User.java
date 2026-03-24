package com.example.studycore.domain.model;

import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.model.enums.ThemePreference;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class User {

    private final UUID id;
    private String name;
    private String email;
    private String passwordHash;
    private final UserRole role;
    private UserStatus status;
    private String avatarUrl;
    private String phone;
    private ThemePreference preferenceTheme;
    private OffsetDateTime lastSeenAt;
    private final OffsetDateTime createdAt;

    private User(
            UUID id,
            String name,
            String email,
            String passwordHash,
            UserRole role,
            UserStatus status,
            String avatarUrl,
            String phone,
            ThemePreference preferenceTheme,
            OffsetDateTime lastSeenAt,
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
        this.preferenceTheme = preferenceTheme != null ? preferenceTheme : ThemePreference.LIGHT;
        this.lastSeenAt = lastSeenAt;
        this.createdAt = createdAt;

        validate();
    }

    public static User create(
            String name,
            String email,
            String passwordHash,
            UserRole role,
            UserStatus status,
            String avatarUrl,
            String phone
    ) {
        final var id = UUID.randomUUID();
        return new User(
                id,
                normalize(name),
                normalize(email).toLowerCase(),
                passwordHash,
                role,
                status,
                avatarUrl,
                normalizePhone(phone),
                ThemePreference.LIGHT,
                null,
                OffsetDateTime.now()
        );
    }

    public static User with(
            UUID id,
            String name,
            String email,
            String passwordHash,
            UserRole role,
            UserStatus status,
            String avatarUrl,
            String phone,
            ThemePreference preferenceTheme,
            OffsetDateTime lastSeenAt,
            OffsetDateTime createdAt
    ) {
        return new User(
                id,
                name,
                email,
                passwordHash,
                role,
                status,
                avatarUrl,
                phone,
                preferenceTheme,
                lastSeenAt,
                createdAt
        );
    }

    public void updateTheme(ThemePreference theme) {
        if (theme == null) throw new IllegalArgumentException("Theme cannot be null.");
        this.preferenceTheme = theme;
    }

    private void validate() {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be blank.");
        }
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be blank.");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null.");
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

