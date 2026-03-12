package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.RefreshToken;
import com.example.studycore.domain.port.RefreshTokenGateway;
import com.example.studycore.infrastructure.mapper.AuthInfraMapper;
import com.example.studycore.infrastructure.persistence.refreshtoken.RefreshTokenEntity;
import com.example.studycore.infrastructure.persistence.refreshtoken.RefreshTokenRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RefreshTokenGatewayImpl implements RefreshTokenGateway {

    private static final AuthInfraMapper AUTH_INFRA_MAPPER = AuthInfraMapper.INSTANCE;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenGatewayImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = AUTH_INFRA_MAPPER.refreshTokenToEntity(refreshToken);
        RefreshTokenEntity savedEntity = refreshTokenRepository.save(entity);
        return AUTH_INFRA_MAPPER.refreshTokenFromEntity(savedEntity);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return refreshTokenRepository.findByTokenHash(tokenHash)
                .map(AUTH_INFRA_MAPPER::refreshTokenFromEntity);
    }

    @Override
    public void revokeByTokenHash(String tokenHash) {
        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(entity -> {
            entity.setRevoked(true);
            refreshTokenRepository.save(entity);
        });
    }
}
