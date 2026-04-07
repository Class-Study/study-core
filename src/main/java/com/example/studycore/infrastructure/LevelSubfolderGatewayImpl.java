package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.LevelSubfolder;
import com.example.studycore.domain.port.LevelSubfolderGateway;
import com.example.studycore.infrastructure.persistence.levelsubfolder.LevelSubfolderEntity;
import com.example.studycore.infrastructure.persistence.levelsubfolder.LevelSubfolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LevelSubfolderGatewayImpl implements LevelSubfolderGateway {

    private final LevelSubfolderRepository levelSubfolderRepository;

    @Override
    public LevelSubfolder save(LevelSubfolder subfolder) {
        final var entity = toEntity(subfolder);
        final var saved = levelSubfolderRepository.save(entity);
        return fromEntity(saved);
    }

    @Override
    public List<LevelSubfolder> findByLevelFolderId(UUID levelFolderId) {
        return levelSubfolderRepository.findByLevelFolderIdOrderByPositionAsc(levelFolderId)
                .stream().map(this::fromEntity).toList();
    }

    @Override
    public Optional<LevelSubfolder> findById(UUID id) {
        return levelSubfolderRepository.findById(id).map(this::fromEntity);
    }

    @Override
    public Optional<LevelSubfolder> findByIdAndLevelFolderId(UUID id, UUID levelFolderId) {
        return levelSubfolderRepository.findByIdAndLevelFolderId(id, levelFolderId).map(this::fromEntity);
    }

    @Override
    public void delete(UUID id) {
        levelSubfolderRepository.deleteById(id);
    }

    @Override
    public int countByLevelFolderId(UUID levelFolderId) {
        return levelSubfolderRepository.countByLevelFolderId(levelFolderId);
    }

    private LevelSubfolder fromEntity(LevelSubfolderEntity entity) {
        return LevelSubfolder.with(
                entity.getId(),
                entity.getLevelFolderId(),
                entity.getName(),
                entity.getPosition(),
                entity.getCreatedBy(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private LevelSubfolderEntity toEntity(LevelSubfolder subfolder) {
        final var entity = new LevelSubfolderEntity();
        entity.setId(subfolder.getId());
        entity.setLevelFolderId(subfolder.getLevelFolderId());
        entity.setName(subfolder.getName());
        entity.setPosition(subfolder.getPosition());
        entity.setCreatedBy(subfolder.getCreatedBy());
        entity.setCreatedAt(subfolder.getCreatedAt());
        entity.setUpdatedAt(subfolder.getUpdatedAt());
        return entity;
    }
}

