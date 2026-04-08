package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.StudentSubfolder;
import com.example.studycore.domain.port.StudentSubfolderGateway;
import com.example.studycore.infrastructure.persistence.studentsubfolder.StudentSubfolderEntity;
import com.example.studycore.infrastructure.persistence.studentsubfolder.StudentSubfolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StudentSubfolderGatewayImpl implements StudentSubfolderGateway {

    private final StudentSubfolderRepository repository;

    @Override
    public StudentSubfolder save(StudentSubfolder subfolder) {
        return fromEntity(repository.save(toEntity(subfolder)));
    }

    @Override
    public List<StudentSubfolder> findByFolderId(UUID folderId) {
        return repository.findByFolderIdOrderByPositionAsc(folderId)
                .stream().map(this::fromEntity).toList();
    }

    @Override
    public Optional<StudentSubfolder> findByFolderIdAndLevelSubfolderId(UUID folderId, UUID levelSubfolderId) {
        return repository.findByFolderIdAndLevelSubfolderId(folderId, levelSubfolderId)
                .map(this::fromEntity);
    }

    @Override
    public List<StudentSubfolder> findByLevelSubfolderId(UUID levelSubfolderId) {
        return repository.findByLevelSubfolderIdOrderByCreatedAtAsc(levelSubfolderId)
                .stream().map(this::fromEntity).toList();
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private StudentSubfolder fromEntity(StudentSubfolderEntity e) {
        return StudentSubfolder.with(e.getId(), e.getFolderId(), e.getLevelSubfolderId(),
                e.getName(), e.getPosition(), e.getCreatedAt(), e.getUpdatedAt());
    }

    private StudentSubfolderEntity toEntity(StudentSubfolder s) {
        final var e = new StudentSubfolderEntity();
        e.setId(s.getId());
        e.setFolderId(s.getFolderId());
        e.setLevelSubfolderId(s.getLevelSubfolderId());
        e.setName(s.getName());
        e.setPosition(s.getPosition());
        e.setCreatedAt(s.getCreatedAt());
        e.setUpdatedAt(s.getUpdatedAt());
        return e;
    }
}

