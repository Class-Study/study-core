package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.LevelFolderTemplate;
import com.example.studycore.domain.port.LevelFolderTemplateGateway;
import com.example.studycore.infrastructure.mapper.LevelFolderTemplateInfraMapper;
import com.example.studycore.infrastructure.persistence.levelfoldertemplate.LevelFolderTemplateRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LevelFolderTemplateGatewayImpl implements LevelFolderTemplateGateway {

    private static final LevelFolderTemplateInfraMapper MAPPER = LevelFolderTemplateInfraMapper.INSTANCE;

    private final LevelFolderTemplateRepository levelFolderTemplateRepository;

    @Override
    public LevelFolderTemplate save(LevelFolderTemplate template) {
        final var saved = levelFolderTemplateRepository.save(MAPPER.toEntity(template));
        return MAPPER.fromEntity(saved);
    }

    @Override
    public List<LevelFolderTemplate> findAllByFolderId(UUID folderId) {
        return levelFolderTemplateRepository.findByLevelFolderIdOrderByCreatedAtAsc(folderId).stream()
                .map(MAPPER::fromEntity)
                .toList();
    }

    @Override
    public Optional<LevelFolderTemplate> findById(UUID id) {
        return levelFolderTemplateRepository.findById(id)
                .map(MAPPER::fromEntity);
    }

    @Override
    public void delete(UUID id) {
        levelFolderTemplateRepository.deleteById(id);
    }
}

