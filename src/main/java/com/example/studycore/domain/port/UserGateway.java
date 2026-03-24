package com.example.studycore.domain.port;

import com.example.studycore.domain.model.User;
import com.example.studycore.domain.model.enums.UserRole;

import java.util.Optional;
import java.util.UUID;

public interface UserGateway {

    Optional<User> findByEmailAndRole(String email, UserRole role);

    Optional<User> findById(UUID id);
}

