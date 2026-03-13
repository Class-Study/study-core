package com.example.studycore.application.usecase.levelprofile;

import com.example.studycore.application.mapper.LevelProfileOutputMapper;
import com.example.studycore.application.usecase.levelprofile.input.UpdateLevelProfileInput;
import com.example.studycore.application.usecase.levelprofile.output.GetLevelProfileOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.LevelFolder;
import com.example.studycore.domain.model.LevelProfile;
import com.example.studycore.domain.port.LevelProfileGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateLevelProfileUseCase {

    private static final LevelProfileOutputMapper MAPPER = LevelProfileOutputMapper.INSTANCE;

    private final LevelProfileGateway levelProfileGateway;

    @Transactional
    public GetLevelProfileOutput execute(UpdateLevelProfileInput input) {
        final var existing = levelProfileGateway.findById(input.id())
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (existing.isSystem()) {
            throw new BusinessException("Perfis de sistema não podem ser alterados");
        }
        if (!input.teacherId().equals(existing.getCreatedBy())) {
            throw new BusinessException("Sem acesso a este perfil de nível.");
        }

        final List<LevelFolder> newFolders = input.folders() == null ? List.of() : input.folders().stream()
                .map(f -> LevelFolder.create(f.name(), f.position(), f.initialFiles()))
                .toList();

        final var updated = LevelProfile.with(
                existing.getId(),
                input.name() != null ? input.name().trim() : existing.getName(),
                existing.getCode(),
                input.icon() != null ? input.icon().trim() : existing.getIcon(),
                input.description() != null ? input.description() : existing.getDescription(),
                existing.isSystem(),
                existing.getCreatedBy(),
                newFolders,
                existing.getCreatedAt()
        );

        return MAPPER.toGetLevelProfileOutput(levelProfileGateway.save(updated));
    }
}
