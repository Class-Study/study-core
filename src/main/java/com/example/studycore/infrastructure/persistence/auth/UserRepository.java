package com.example.studycore.infrastructure.persistence.auth;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmailIgnoreCase(String email);

    Optional<UserEntity> findByEmailIgnoreCaseAndRole(String email, String role);

    List<UserEntity> findByRole(String role, Sort sort);
}

