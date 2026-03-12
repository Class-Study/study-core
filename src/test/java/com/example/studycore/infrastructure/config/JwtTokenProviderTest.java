package com.example.studycore.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.studycore.domain.model.User;
import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.model.enums.UserStatus;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    @Test
    void shouldGenerateAndValidateAccessToken() {
        JwtTokenProvider provider = new JwtTokenProvider(
                "dev-secret-key-must-be-at-least-256-bits-long!",
                15
        );

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .name("John")
                .email("john@example.com")
                .role(UserRole.STUDENT)
                .status(UserStatus.ACTIVE)
                .build();

        String token = provider.generateAccessToken(user);

        assertTrue(provider.validateToken(token));
        assertEquals(userId, provider.extractUserId(token));
        assertEquals("john@example.com", provider.extractEmail(token));
        assertEquals("STUDENT", provider.extractRole(token));
    }
}

