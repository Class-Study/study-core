package com.example.studycore.application.usecase.levelprofile;

import com.example.studycore.application.mapper.LevelProfileOutputMapper;
import com.example.studycore.application.usecase.levelprofile.output.ListLevelProfilesOutput;
import com.example.studycore.application.usecase.levelprofile.output.LevelFolderOutput;
import com.example.studycore.application.usecase.levelprofile.output.LevelFolderTemplateOutput;
import com.example.studycore.domain.port.LevelFolderTemplateGateway;
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
    private final LevelFolderTemplateGateway levelFolderTemplateGateway;

    public ListLevelProfilesOutput execute(UUID teacherId) {
        final var teacherProfiles = levelProfileGateway.findAllByCreatedBy(teacherId);
        final var systemProfiles = levelProfileGateway.findSystemProfiles();

        final var merged = new ArrayList<>(systemProfiles);
        merged.addAll(teacherProfiles);
        merged.sort(Comparator
                .comparing(com.example.studycore.domain.model.LevelProfile::isSystem).reversed()
                .thenComparing(com.example.studycore.domain.model.LevelProfile::getName, String.CASE_INSENSITIVE_ORDER));

        final var output = MAPPER.toListLevelProfilesOutput(merged);

        final var itemsWithTemplates = output.levelProfiles().stream()
                .map(item -> new ListLevelProfilesOutput.Item(
                        item.id(),
                        item.name(),
                        item.code(),
                        item.icon(),
                        item.description(),
                        item.isSystem(),
                        item.createdBy(),
                        item.folders().stream()
                                .map(folder -> new LevelFolderOutput(
                                        folder.id(),
                                        folder.name(),
                                        folder.position(),
                                        folder.initialFiles(),
                                        levelFolderTemplateGateway.findAllByFolderId(folder.id()).stream()
                                                .map(template -> new LevelFolderTemplateOutput(
                                                        template.getId(),
                                                        template.getLevelFolderId(),
                                                        template.getTitle(),
                                                        template.getType(),
                                                        template.getOriginalFilename(),
                                                        template.getCreatedAt()
                                                ))
                                                .toList()
                                ))
                                .toList(),
                        item.createdAt()
                ))
                .toList();

        return new ListLevelProfilesOutput(itemsWithTemplates);
    }
}
