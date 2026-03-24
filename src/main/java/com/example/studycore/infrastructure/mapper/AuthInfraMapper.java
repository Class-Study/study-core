package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.auth.input.LoginInput;
import com.example.studycore.application.usecase.auth.input.RefreshTokenInput;
import com.example.studycore.application.usecase.auth.output.LoginOutput;
import com.example.studycore.application.usecase.auth.output.TokenOutput;
import com.example.studycore.domain.model.RefreshToken;
import com.example.studycore.domain.model.User;
import com.example.studycore.domain.model.enums.ThemePreference;
import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.infrastructure.api.controllers.auth.request.LoginRequest;
import com.example.studycore.infrastructure.api.controllers.auth.request.RefreshRequest;
import com.example.studycore.infrastructure.api.controllers.auth.response.AuthResponse;
import com.example.studycore.infrastructure.api.controllers.auth.response.LoginResponse;
import com.example.studycore.infrastructure.api.controllers.auth.response.TokenResponse;
import com.example.studycore.infrastructure.persistence.auth.UserEntity;
import com.example.studycore.infrastructure.persistence.refreshtoken.RefreshTokenEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthInfraMapper {

    AuthInfraMapper INSTANCE = Mappers.getMapper(AuthInfraMapper.class);

    default LoginInput toLoginInput(LoginRequest request, String userAgent, UserRole expectedRole) {
        if (request == null) return null;
        return new LoginInput(request.email(), request.password(), userAgent, expectedRole);
    }

    RefreshTokenInput toRefreshTokenInput(RefreshRequest request);

    default RefreshTokenInput toRefreshTokenInput(String refreshToken) {
        if (refreshToken == null) return null;
        return new RefreshTokenInput(refreshToken);
    }

    // ===== OUTPUT → RESPONSE MAPPINGS =====
    @Mapping(target = "message", constant = "Login successful")
    @Mapping(target = "user.id", source = "userId", qualifiedByName = "uuidToString")
    @Mapping(target = "user.name", source = "userName")
    @Mapping(target = "user.email", source = "userEmail")
    @Mapping(target = "user.role", source = "userRole")
    @Mapping(target = "user.preferenceTheme", expression = "java(loginOutput.preferenceTheme() != null ? loginOutput.preferenceTheme().name().toLowerCase() : \"light\")")
    AuthResponse toAuthResponse(LoginOutput loginOutput);

    @Mapping(target = "message", constant = "Token refreshed successfully")
    @Mapping(target = "user", ignore = true)
    AuthResponse toAuthResponse(TokenOutput output);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "user.name", source = "userName")
    @Mapping(target = "user.email", source = "userEmail")
    @Mapping(target = "user.role", source = "userRole")
    LoginResponse toLoginResponse(LoginOutput output);

    TokenResponse toTokenResponse(TokenOutput output);

    // ===== ENTITY ↔ DOMAIN MAPPINGS =====
    // Entity → Domain using factory methods (leveraging domain model patterns)
    default User userFromEntity(UserEntity entity) {
        if (entity == null) return null;
        return User.with(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                UserRole.valueOf(entity.getRole()),
                UserStatus.valueOf(entity.getStatus()),
                entity.getAvatarUrl(),
                entity.getPhone(),
                entity.getPreferenceTheme() != null
                        ? ThemePreference.valueOf(entity.getPreferenceTheme().toUpperCase())
                        : ThemePreference.LIGHT,
                entity.getLastSeenAt(),
                entity.getCreatedAt()
        );
    }

    default RefreshToken refreshTokenFromEntity(RefreshTokenEntity entity) {
        if (entity == null) return null;
        return RefreshToken.with(
                entity.getId(),
                entity.getUserId(),
                entity.getTokenHash(),
                entity.getExpiresAt(),
                entity.isRevoked(),
                entity.getCreatedAt(),
                entity.getUserAgent()
        );
    }

    // Domain → Entity using automatic mapping with enum conversion
    @Mapping(target = "role", expression = "java(user.getRole().name())")
    @Mapping(target = "status", expression = "java(user.getStatus().name())")
    @Mapping(target = "preferenceTheme", expression = "java(user.getPreferenceTheme().name().toLowerCase())")
    UserEntity userToEntity(User user);

    RefreshTokenEntity refreshTokenToEntity(RefreshToken refreshToken);

    // ===== HELPER METHODS =====
    @Named("uuidToString")
    default String uuidToString(java.util.UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }

}
