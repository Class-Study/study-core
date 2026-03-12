package com.example.studycore.application.usecase.auth;

import com.example.studycore.application.mapper.AuthOutputMapper;
import com.example.studycore.application.usecase.auth.input.LoginInput;
import com.example.studycore.application.usecase.auth.output.LoginOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.UnauthorizedException;
import com.example.studycore.domain.model.RefreshToken;
import com.example.studycore.domain.model.User;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.RefreshTokenGateway;
import com.example.studycore.domain.port.UserGateway;
import com.example.studycore.infrastructure.config.JwtTokenProvider;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private static final AuthOutputMapper MAPPER = AuthOutputMapper.INSTANCE;
    private static final int REFRESH_TOKEN_EXPIRATION_DAYS = 30;

    private final UserGateway userGateway;
    private final RefreshTokenGateway refreshTokenGateway;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public LoginOutput execute(LoginInput input) {
        User user = userGateway.findByEmail(input.email().trim().toLowerCase())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials."));

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new BusinessException("User is blocked.");
        }

        if (!passwordEncoder.matches(input.password(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials.");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshTokenRaw = UUID.randomUUID().toString();
        RefreshToken refreshToken = RefreshToken.newToken(
                user.getId(),
                sha256(refreshTokenRaw),
                Instant.now().plus(REFRESH_TOKEN_EXPIRATION_DAYS, ChronoUnit.DAYS),
                input.userAgent()
        );
        refreshTokenGateway.save(refreshToken);

        return MAPPER.toLoginOutput(
                user,
                accessToken,
                refreshTokenRaw,
                jwtTokenProvider.getAccessTokenExpirationSeconds()
        );
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available.", e);
        }
    }
}

