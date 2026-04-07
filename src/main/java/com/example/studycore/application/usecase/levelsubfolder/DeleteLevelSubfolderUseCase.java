package com.example.studycore.application.usecase.levelsubfolder;

import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.LevelSubfolderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteLevelSubfolderUseCase {

    private final LevelSubfolderGateway levelSubfolderGateway;
    private final LevelProfileGateway levelProfileGateway;

    @Transactional
    public void execute(UUID levelProfileId, UUID levelFolderId, UUID subfolderId, UUID requestedBy) {
        final var levelProfile = levelProfileGateway.findById(levelProfileId)
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!levelProfile.isSystem() && !requestedBy.equals(levelProfile.getCreatedBy())) {
            throw new BusinessException("Perfil de nível não pertence ao professor autenticado.");
        }

        if (levelProfile.getFolders().stream().noneMatch(f -> f.getId().equals(levelFolderId))) {
            throw new NotFoundException("Pasta de nível não encontrada neste perfil.");
        }

        levelSubfolderGateway.findByIdAndLevelFolderId(subfolderId, levelFolderId)
                .orElseThrow(() -> new NotFoundException("Subpasta não encontrada nesta pasta de nível."));

        levelSubfolderGateway.delete(subfolderId);
    }
}

