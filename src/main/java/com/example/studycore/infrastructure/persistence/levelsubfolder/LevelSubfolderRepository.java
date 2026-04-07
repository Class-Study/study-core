package com.example.studycore.infrastructure.persistence.levelsubfolder;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LevelSubfolderRepository extends JpaRepository<LevelSubfolderEntity, UUID> {

    List<LevelSubfolderEntity> findByLevelFolderIdOrderByPositionAsc(UUID levelFolderId);

    Optional<LevelSubfolderEntity> findByIdAndLevelFolderId(UUID id, UUID levelFolderId);

    int countByLevelFolderId(UUID levelFolderId);
}

