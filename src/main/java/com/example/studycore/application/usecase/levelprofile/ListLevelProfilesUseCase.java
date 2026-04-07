package com.example.studycore.application.usecase.levelprofile;

import com.example.studycore.application.mapper.LevelProfileOutputMapper;
import com.example.studycore.application.mapper.StudyMaterialOutputMapper;
import com.example.studycore.application.usecase.levelprofile.output.LevelFolderOutput;
import com.example.studycore.application.usecase.levelprofile.output.LevelFolderTemplateOutput;
import com.example.studycore.application.usecase.levelprofile.output.ListLevelProfilesOutput;
import com.example.studycore.application.usecase.levelprofile.output.SubfolderOutput;
import com.example.studycore.domain.port.LevelFolderTemplateGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.LevelSubfolderGateway;
import com.example.studycore.domain.port.StudyMaterialGateway;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListLevelProfilesUseCase {

    private static final LevelProfileOutputMapper MAPPER = LevelProfileOutputMapper.INSTANCE;
    private static final StudyMaterialOutputMapper STUDY_MATERIAL_MAPPER = StudyMaterialOutputMapper.INSTANCE;

    private final LevelProfileGateway levelProfileGateway;
    private final LevelFolderTemplateGateway levelFolderTemplateGateway;
    private final LevelSubfolderGateway levelSubfolderGateway;
    private final StudyMaterialGateway studyMaterialGateway;

    public ListLevelProfilesOutput execute(UUID teacherId) {
        final var teacherProfiles = levelProfileGateway.findAllByCreatedBy(teacherId);
        final var systemProfiles = levelProfileGateway.findSystemProfiles();

        final var merged = new ArrayList<>(systemProfiles);
        merged.addAll(teacherProfiles);
        merged.sort(Comparator
                .comparing(com.example.studycore.domain.model.LevelProfile::isSystem).reversed()
                .thenComparing(com.example.studycore.domain.model.LevelProfile::getName, String.CASE_INSENSITIVE_ORDER));

        final var output = MAPPER.toListLevelProfilesOutput(merged);

        final var itemsWithSubfolders = output.levelProfiles().stream()
                .map(item -> new ListLevelProfilesOutput.Item(
                        item.id(),
                        item.name(),
                        item.code(),
                        item.icon(),
                        item.description(),
                        item.isSystem(),
                        item.createdBy(),
                        item.folders().stream()
                                .map(folder -> buildFolderOutput(folder))
                                .toList(),
                        item.createdAt()
                ))
                .toList();

        return new ListLevelProfilesOutput(itemsWithSubfolders);
    }

    private LevelFolderOutput buildFolderOutput(LevelFolderOutput folder) {
        final var legacyTemplates = levelFolderTemplateGateway.findAllByFolderId(folder.id()).stream()
                .filter(t -> t.getSubfolderId() == null)
                .map(t -> new LevelFolderTemplateOutput(
                        t.getId(), t.getLevelFolderId(), t.getTitle(),
                        t.getType(), t.getOriginalFilename(), t.getConvertedHtml(), t.getCreatedAt()))
                .toList();

        final var subfolders = levelSubfolderGateway.findByLevelFolderId(folder.id())
                .stream()
                .map(subfolder -> {
                    final var templates = levelFolderTemplateGateway
                            .findBySubfolderId(subfolder.getId())
                            .stream()
                            .map(t -> new LevelFolderTemplateOutput(
                                    t.getId(), t.getLevelFolderId(), t.getTitle(),
                                    t.getType(), t.getOriginalFilename(), t.getConvertedHtml(), t.getCreatedAt()))
                            .toList();
                    final var materials = studyMaterialGateway
                            .findBySubfolderId(subfolder.getId())
                            .stream()
                            .map(STUDY_MATERIAL_MAPPER::toOutput)
                            .toList();
                    return new SubfolderOutput(
                            subfolder.getId(), subfolder.getName(), subfolder.getPosition(),
                            templates, materials, subfolder.getCreatedAt(), subfolder.getUpdatedAt());
                })
                .toList();

        return new LevelFolderOutput(
                folder.id(), folder.name(), folder.position(),
                legacyTemplates.size(), legacyTemplates, subfolders);
    }
}
