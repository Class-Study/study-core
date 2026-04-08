package com.example.studycore.domain.port;

import com.example.studycore.domain.model.StudentSubfolder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentSubfolderGateway {
    StudentSubfolder save(StudentSubfolder subfolder);
    List<StudentSubfolder> findByFolderId(UUID folderId);
    Optional<StudentSubfolder> findByFolderIdAndLevelSubfolderId(UUID folderId, UUID levelSubfolderId);
    List<StudentSubfolder> findByLevelSubfolderId(UUID levelSubfolderId);
    void delete(UUID id);
}

