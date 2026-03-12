package com.example.studycore.domain.model;

import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.model.enums.UserStatus;
import java.time.Instant;
import java.util.UUID;

public class User {

    private UUID id;
    private String name;
    private String email;
    private String passwordHash;
    private UserRole role;
    private UserStatus status;
    private String avatarUrl;
    private String phone;
    private String timezone;
    private Instant lastSeenAt;
    private Instant createdAt;

    private User() {
    }

    public static User with(UUID id, String name, String email,
                            String passwordHash, UserRole role,
                            UserStatus status, String avatarUrl,
                            String phone, String timezone,
                            Instant lastSeenAt, Instant createdAt) {
        final var user = new User();
        user.id = id;
        user.name = name;
        user.email = email;
        user.passwordHash = passwordHash;
        user.role = role;
        user.status = status;
        user.avatarUrl = avatarUrl;
        user.phone = phone;
        user.timezone = timezone;
        user.lastSeenAt = lastSeenAt;
        user.createdAt = createdAt;
        return user;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getPhone() {
        return phone;
    }

    public String getTimezone() {
        return timezone;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

