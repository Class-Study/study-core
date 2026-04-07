package com.example.studycore.domain.port;

import com.example.studycore.domain.model.StudyMaterial;
import com.example.studycore.domain.model.enums.SubfolderType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudyMaterialGateway {
    StudyMaterial save(StudyMaterial studyMaterial);
    List<StudyMaterial> findByLevelFolderAndSubfolder(UUID levelFolderId, SubfolderType subfolderType);
    List<StudyMaterial> findBySubfolderId(UUID subfolderId);
    List<StudyMaterial> findAllByLevelFolderId(UUID levelFolderId);
    Optional<StudyMaterial> findById(UUID id);
    void delete(UUID id);
}

