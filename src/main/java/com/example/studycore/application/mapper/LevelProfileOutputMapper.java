package com.example.studycore.application.mapper;

import com.example.studycore.application.usecase.levelprofile.output.GetLevelProfileOutput;
import com.example.studycore.application.usecase.levelprofile.output.LevelFolderOutput;
import com.example.studycore.application.usecase.levelprofile.output.ListLevelProfilesOutput;
import com.example.studycore.domain.model.LevelFolder;
import com.example.studycore.domain.model.LevelProfile;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LevelProfileOutputMapper {

    LevelProfileOutputMapper INSTANCE = Mappers.getMapper(LevelProfileOutputMapper.class);

    default GetLevelProfileOutput toGetLevelProfileOutput(LevelProfile levelProfile) {
        if (levelProfile == null) return null;
        return new GetLevelProfileOutput(
                levelProfile.getId(),
                levelProfile.getName(),
                levelProfile.getCode(),
                levelProfile.getIcon(),
                levelProfile.getDescription(),
                levelProfile.isSystem(),
                levelProfile.getCreatedBy(),
                toFolderOutputs(levelProfile.getFolders()),
                levelProfile.getCreatedAt()
        );
    }

    default ListLevelProfilesOutput toListLevelProfilesOutput(List<LevelProfile> profiles) {
        final var items = profiles == null ? List.<ListLevelProfilesOutput.Item>of() : profiles.stream()
                .map(profile -> new ListLevelProfilesOutput.Item(
                        profile.getId(),
                        profile.getName(),
                        profile.getCode(),
                        profile.getIcon(),
                        profile.getDescription(),
                        profile.isSystem(),
                        profile.getCreatedBy(),
                        toFolderOutputs(profile.getFolders()),
                        profile.getCreatedAt()
                ))
                .toList();
        return new ListLevelProfilesOutput(items);
    }

    default List<LevelFolderOutput> toFolderOutputs(List<LevelFolder> folders) {
        return folders == null ? List.of() : folders.stream()
                .map(folder -> new LevelFolderOutput(
                        folder.getId(),
                        folder.getName(),
                        folder.getPosition(),
                        folder.getInitialFiles()
                ))
                .toList();
    }
}

