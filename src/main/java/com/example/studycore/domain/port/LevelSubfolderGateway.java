package com.example.studycore.domain.port;

import com.example.studycore.domain.model.LevelSubfolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LevelSubfolderGateway {
    LevelSubfolder save(LevelSubfolder subfolder);
    List<LevelSubfolder> findByLevelFolderId(UUID levelFolderId);
    Optional<LevelSubfolder> findById(UUID id);
    Optional<LevelSubfolder> findByIdAndLevelFolderId(UUID id, UUID levelFolderId);
    void delete(UUID id);
    int countByLevelFolderId(UUID levelFolderId);
}

