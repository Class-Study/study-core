package com.example.studycore.application.usecase.levelfoldertemplate;

import com.example.studycore.application.mapper.LevelFolderTemplateOutputMapper;
import com.example.studycore.application.usecase.levelfoldertemplate.output.LevelFolderTemplateOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.LevelFolderTemplateGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListLevelFolderTemplatesUseCase {

    private static final LevelFolderTemplateOutputMapper MAPPER = LevelFolderTemplateOutputMapper.INSTANCE;

    private final LevelFolderTemplateGateway levelFolderTemplateGateway;
    private final LevelProfileGateway levelProfileGateway;

    public List<LevelFolderTemplateOutput> execute(UUID levelProfileId, UUID levelFolderId, UUID teacherId) {
        final var levelProfile = levelProfileGateway.findById(levelProfileId)
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!levelProfile.isSystem() && !teacherId.equals(levelProfile.getCreatedBy())) {
            throw new BusinessException("Perfil de nível não pertence ao professor autenticado.");
        }

        if (levelProfile.getFolders().stream()
                .noneMatch(f -> f.getId().equals(levelFolderId))) {
            throw new NotFoundException("Pasta de nível não encontrada neste perfil.");
        }

        return levelFolderTemplateGateway.findAllByFolderId(levelFolderId).stream()
                .map(MAPPER::toOutput)
                .toList();
    }
}

