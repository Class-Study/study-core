package com.example.studycore.application.usecase.levelprofile;

import com.example.studycore.application.mapper.LevelProfileOutputMapper;
import com.example.studycore.application.usecase.levelprofile.input.CreateLevelProfileInput;
import com.example.studycore.application.usecase.levelprofile.output.GetLevelProfileOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.model.LevelFolder;
import com.example.studycore.domain.model.LevelProfile;
import com.example.studycore.domain.port.LevelProfileGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateLevelProfileUseCase {

    private static final LevelProfileOutputMapper MAPPER = LevelProfileOutputMapper.INSTANCE;

    private final LevelProfileGateway levelProfileGateway;

    @Transactional
    public GetLevelProfileOutput execute(final CreateLevelProfileInput input) {
        levelProfileGateway.findByCode(input.code().trim()).ifPresent(existing -> {
            if (!existing.isSystem() && input.teacherId().equals(existing.getCreatedBy())) {
                throw new BusinessException("Já existe perfil com esse code para este professor.");
            }
            if (existing.isSystem()) {
                throw new BusinessException("Code já utilizado por perfil de sistema.");
            }
        });

        final List<LevelFolder> folders = input.folders() == null ? List.of() : input.folders().stream()
                .map(f -> LevelFolder.create(f.name(), f.position(), f.initialFiles()))
                .toList();

        final var toSave = LevelProfile.create(
                input.name(),
                input.code(),
                input.icon(),
                input.description(),
                false,
                input.teacherId(),
                folders
        );

        return MAPPER.toGetLevelProfileOutput(levelProfileGateway.save(toSave));
    }
}
