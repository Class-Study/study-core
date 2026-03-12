package com.example.studycore.domain.port;

import com.example.studycore.domain.model.User;
import java.util.Optional;
import java.util.UUID;

public interface UserGateway {

    Optional<User> findByEmail(String email);

    Optional<User> findById(UUID id);
}

