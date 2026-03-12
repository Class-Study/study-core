package com.example.studycore.application.usecase.auth;

import com.example.studycore.application.usecase.auth.input.RefreshTokenInput;
import com.example.studycore.domain.port.RefreshTokenGateway;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutUseCase {

    private final RefreshTokenGateway refreshTokenGateway;

    @Transactional
    public void execute(RefreshTokenInput input) {
        refreshTokenGateway.revokeByTokenHash(sha256(input.refreshToken().trim()));
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

