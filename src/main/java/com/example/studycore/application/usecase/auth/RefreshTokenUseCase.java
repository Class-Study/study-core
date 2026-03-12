package com.example.studycore.application.usecase.auth;

import com.example.studycore.application.usecase.auth.input.RefreshTokenInput;
import com.example.studycore.application.usecase.auth.output.TokenOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
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
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenUseCase {

    private final RefreshTokenGateway refreshTokenGateway;
    private final UserGateway userGateway;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public TokenOutput execute(RefreshTokenInput input) {
        String refreshTokenRaw = input.refreshToken().trim();
        RefreshToken refreshToken = refreshTokenGateway.findByTokenHash(sha256(refreshTokenRaw))
                .orElseThrow(() -> new BusinessException("Invalid refresh token."));

        if (refreshToken.isRevoked() || refreshToken.isExpired(Instant.now())) {
            throw new BusinessException("Refresh token is invalid or expired.");
        }

        User user = userGateway.findById(refreshToken.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found."));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BusinessException("User is not active.");
        }

        return new TokenOutput(
                jwtTokenProvider.generateAccessToken(user),
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

