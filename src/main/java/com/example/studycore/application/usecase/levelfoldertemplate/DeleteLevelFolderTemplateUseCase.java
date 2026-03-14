package com.example.studycore.application.usecase.levelfoldertemplate;

import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.LevelFolderTemplateGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteLevelFolderTemplateUseCase {

    private final LevelFolderTemplateGateway levelFolderTemplateGateway;
    private final LevelProfileGateway levelProfileGateway;

    public void execute(UUID levelProfileId, UUID levelFolderId, UUID templateId, UUID teacherId) {
        final var levelProfile = levelProfileGateway.findById(levelProfileId)
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!levelProfile.isSystem() && !teacherId.equals(levelProfile.getCreatedBy())) {
            throw new BusinessException("Perfil de nível não pertence ao professor autenticado.");
        }

        if (levelProfile.getFolders().stream()
                .noneMatch(f -> f.getId().equals(levelFolderId))) {
            throw new NotFoundException("Pasta de nível não encontrada neste perfil.");
        }

        final var template = levelFolderTemplateGateway.findById(templateId)
                .orElseThrow(() -> new NotFoundException("Template não encontrado."));

        if (!template.getLevelFolderId().equals(levelFolderId)) {
            throw new BusinessException("Template não pertence a esta pasta de nível.");
        }

        levelFolderTemplateGateway.delete(templateId);
    }
}

