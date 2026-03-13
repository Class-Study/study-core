package com.example.studycore.infrastructure.persistence.levelprofile;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelFolderRepository extends JpaRepository<LevelFolderEntity, UUID> {
    List<LevelFolderEntity> findByLevelProfileIdOrderByPositionAsc(UUID levelProfileId);
    void deleteByLevelProfileId(UUID levelProfileId);
}

