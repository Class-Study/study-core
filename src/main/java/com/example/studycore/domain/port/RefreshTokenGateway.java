package com.example.studycore.domain.port;

import com.example.studycore.domain.model.RefreshToken;
import java.util.Optional;

public interface RefreshTokenGateway {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void revokeByTokenHash(String tokenHash);
}

