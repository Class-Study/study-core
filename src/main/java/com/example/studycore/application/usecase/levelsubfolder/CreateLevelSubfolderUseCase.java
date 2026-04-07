package com.example.studycore.application.usecase.levelsubfolder;

import com.example.studycore.application.usecase.levelsubfolder.input.CreateLevelSubfolderInput;
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
public class CreateLevelSubfolderUseCase {

    private final LevelSubfolderGateway levelSubfolderGateway;
    private final LevelProfileGateway levelProfileGateway;

    @Transactional
    public LevelSubfolderOutput execute(CreateLevelSubfolderInput input) {
        final var levelProfile = levelProfileGateway.findById(input.levelProfileId())
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!levelProfile.isSystem() && !input.createdBy().equals(levelProfile.getCreatedBy())) {
            throw new BusinessException("Perfil de nível não pertence ao professor autenticado.");
        }

        if (levelProfile.getFolders().stream().noneMatch(f -> f.getId().equals(input.levelFolderId()))) {
            throw new NotFoundException("Pasta de nível não encontrada neste perfil.");
        }

        final var position = input.position() != null
                ? input.position()
                : levelSubfolderGateway.countByLevelFolderId(input.levelFolderId()) + 1;

        final var subfolder = LevelSubfolder.create(
                input.levelFolderId(),
                input.name(),
                position,
                input.createdBy()
        );

        final var saved = levelSubfolderGateway.save(subfolder);
        return toOutput(saved);
    }

    private LevelSubfolderOutput toOutput(LevelSubfolder s) {
        return new LevelSubfolderOutput(s.getId(), s.getLevelFolderId(), s.getName(),
                s.getPosition(), s.getCreatedBy(), s.getCreatedAt(), s.getUpdatedAt());
    }
}

