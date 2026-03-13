package com.example.studycore.infrastructure.persistence.levelprofile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelProfileRepository extends JpaRepository<LevelProfileEntity, UUID> {
    Optional<LevelProfileEntity> findByCodeIgnoreCase(String code);
    List<LevelProfileEntity> findByCreatedBy(UUID createdBy);
    List<LevelProfileEntity> findByIsSystemTrue();
}

