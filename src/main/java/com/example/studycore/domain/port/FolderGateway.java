package com.example.studycore.domain.port;

import com.example.studycore.domain.model.Folder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FolderGateway {
    Folder save(Folder folder);
    List<Folder> saveAll(List<Folder> folders);
    Optional<Folder> findById(UUID id);
    List<Folder> findByStudentId(UUID studentId);
    void delete(UUID id);
}

