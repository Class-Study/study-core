package com.example.studycore.domain.port;

import com.example.studycore.domain.model.LevelFolderTemplate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LevelFolderTemplateGateway {
    LevelFolderTemplate save(LevelFolderTemplate template);
    List<LevelFolderTemplate> findAllByFolderId(UUID folderId);
    Optional<LevelFolderTemplate> findById(UUID id);
    void delete(UUID id);
}

