package com.example.studycore.application.usecase.levelsubfolder;

import com.example.studycore.application.usecase.levelsubfolder.input.UpdateLevelSubfolderInput;
import com.example.studycore.application.usecase.levelsubfolder.output.LevelSubfolderOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.LevelSubfolder;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.LevelSubfolderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateLevelSubfolderUseCase {

    private final LevelSubfolderGateway levelSubfolderGateway;
    private final LevelProfileGateway levelProfileGateway;

    @Transactional
    public LevelSubfolderOutput execute(UpdateLevelSubfolderInput input) {
        final var levelProfile = levelProfileGateway.findById(input.levelProfileId())
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!levelProfile.isSystem() && !input.updatedBy().equals(levelProfile.getCreatedBy())) {
            throw new BusinessException("Perfil de nível não pertence ao professor autenticado.");
        }

        if (levelProfile.getFolders().stream().noneMatch(f -> f.getId().equals(input.levelFolderId()))) {
            throw new NotFoundException("Pasta de nível não encontrada neste perfil.");
        }

        final var existing = levelSubfolderGateway.findByIdAndLevelFolderId(input.subfolderId(), input.levelFolderId())
                .orElseThrow(() -> new NotFoundException("Subpasta não encontrada nesta pasta de nível."));

        final var updated = existing.update(input.name(), input.position());
        final var saved = levelSubfolderGateway.save(updated);
        return toOutput(saved);
    }

    private LevelSubfolderOutput toOutput(LevelSubfolder s) {
        return new LevelSubfolderOutput(s.getId(), s.getLevelFolderId(), s.getName(),
                s.getPosition(), s.getCreatedBy(), s.getCreatedAt(), s.getUpdatedAt());
    }
}

