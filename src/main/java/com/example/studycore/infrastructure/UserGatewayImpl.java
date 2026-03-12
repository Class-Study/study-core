package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.User;
import com.example.studycore.domain.port.UserGateway;
import com.example.studycore.infrastructure.mapper.AuthInfraMapper;
import com.example.studycore.infrastructure.persistence.auth.UserEntity;
import com.example.studycore.infrastructure.persistence.auth.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserGatewayImpl implements UserGateway {

    private static final AuthInfraMapper AUTH_INFRA_MAPPER = AuthInfraMapper.INSTANCE;

    private final UserRepository userRepository;

    public UserGatewayImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(AUTH_INFRA_MAPPER::userFromEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id)
                .map(AUTH_INFRA_MAPPER::userFromEntity);
    }

    public User save(User user) {
        UserEntity entity = AUTH_INFRA_MAPPER.userToEntity(user);
        UserEntity savedEntity = userRepository.save(entity);
        return AUTH_INFRA_MAPPER.userFromEntity(savedEntity);
    }
}
