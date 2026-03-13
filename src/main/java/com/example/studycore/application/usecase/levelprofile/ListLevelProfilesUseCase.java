package com.example.studycore.application.usecase.levelprofile;

import com.example.studycore.application.mapper.LevelProfileOutputMapper;
import com.example.studycore.application.usecase.levelprofile.output.ListLevelProfilesOutput;
import com.example.studycore.domain.port.LevelProfileGateway;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListLevelProfilesUseCase {

    private static final LevelProfileOutputMapper MAPPER = LevelProfileOutputMapper.INSTANCE;

    private final LevelProfileGateway levelProfileGateway;

    public ListLevelProfilesOutput execute(UUID teacherId) {
        final var teacherProfiles = levelProfileGateway.findAllByCreatedBy(teacherId);
        final var systemProfiles = levelProfileGateway.findSystemProfiles();

        final var merged = new ArrayList<>(systemProfiles);
        merged.addAll(teacherProfiles);
        merged.sort(Comparator
                .comparing(com.example.studycore.domain.model.LevelProfile::isSystem).reversed()
                .thenComparing(com.example.studycore.domain.model.LevelProfile::getName, String.CASE_INSENSITIVE_ORDER));

        return MAPPER.toListLevelProfilesOutput(merged);
    }
}
