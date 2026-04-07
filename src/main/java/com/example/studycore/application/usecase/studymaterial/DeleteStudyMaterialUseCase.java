package com.example.studycore.application.usecase.studymaterial;

import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.StudyMaterialGateway;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteStudyMaterialUseCase {

    private final StudyMaterialGateway studyMaterialGateway;
    private final LevelProfileGateway levelProfileGateway;

    @Transactional
    public void execute(UUID levelProfileId, UUID levelFolderId, UUID materialId, UUID requestedBy) {
        final var levelProfile = levelProfileGateway.findById(levelProfileId)
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!levelProfile.isSystem() && !requestedBy.equals(levelProfile.getCreatedBy())) {
            throw new BusinessException("Perfil de nível não pertence ao professor autenticado.");
        }

        if (levelProfile.getFolders().stream().noneMatch(f -> f.getId().equals(levelFolderId))) {
            throw new NotFoundException("Pasta de nível não encontrada neste perfil.");
        }

        final var material = studyMaterialGateway.findById(materialId)
                .orElseThrow(() -> new NotFoundException("Material de estudo não encontrado."));

        if (!material.getLevelFolderId().equals(levelFolderId)) {
            throw new BusinessException("Material não pertence a esta pasta de nível.");
        }

        studyMaterialGateway.delete(materialId);
    }
}

