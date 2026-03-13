package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.levelprofile.input.CreateLevelFolderInput;
import com.example.studycore.application.usecase.levelprofile.input.CreateLevelProfileInput;
import com.example.studycore.application.usecase.levelprofile.input.UpdateLevelProfileInput;
import com.example.studycore.application.usecase.levelprofile.output.GetLevelProfileOutput;
import com.example.studycore.application.usecase.levelprofile.output.LevelFolderOutput;
import com.example.studycore.application.usecase.levelprofile.output.ListLevelProfilesOutput;
import com.example.studycore.domain.model.LevelFolder;
import com.example.studycore.domain.model.LevelProfile;
import com.example.studycore.infrastructure.api.controllers.levelprofile.request.CreateLevelFolderRequest;
import com.example.studycore.infrastructure.api.controllers.levelprofile.request.CreateLevelProfileRequest;
import com.example.studycore.infrastructure.api.controllers.levelprofile.request.UpdateLevelProfileRequest;
import com.example.studycore.infrastructure.api.controllers.levelprofile.response.GetLevelProfileResponse;
import com.example.studycore.infrastructure.api.controllers.levelprofile.response.LevelFolderResponse;
import com.example.studycore.infrastructure.api.controllers.levelprofile.response.ListLevelProfilesResponse;
import com.example.studycore.infrastructure.persistence.levelprofile.LevelFolderEntity;
import com.example.studycore.infrastructure.persistence.levelprofile.LevelProfileEntity;

import java.util.List;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LevelProfileInfraMapper {

    LevelProfileInfraMapper INSTANCE = Mappers.getMapper(LevelProfileInfraMapper.class);

    default LevelProfile fromEntity(final LevelProfileEntity entity, final List<LevelFolderEntity> folders) {
        if (entity == null) return null;
        return LevelProfile.with(
                entity.getId(),
                entity.getName(),
                entity.getCode(),
                entity.getIcon(),
                entity.getDescription(),
                entity.isSystem(),
                entity.getCreatedBy(),
                folders == null ? List.of() : folders.stream()
                        .map(folder -> LevelFolder.with(folder.getId(), folder.getName(), folder.getPosition(), folder.getInitialFiles()))
                        .toList(),
                entity.getCreatedAt()
        );
    }

    default LevelProfileEntity toEntity(final LevelProfile levelProfile) {
        if (levelProfile == null) return null;
        final var entity = new LevelProfileEntity();
        entity.setId(levelProfile.getId());
        entity.setName(levelProfile.getName());
        entity.setCode(levelProfile.getCode());
        entity.setIcon(levelProfile.getIcon());
        entity.setDescription(levelProfile.getDescription());
        entity.setSystem(levelProfile.isSystem());
        entity.setCreatedBy(levelProfile.getCreatedBy());
        entity.setCreatedAt(levelProfile.getCreatedAt());
        return entity;
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "position", source = "position")
    @Mapping(target = "initialFiles", source = "initialFiles")
    @Mapping(target = "levelProfileId", ignore = true)
    LevelFolderEntity toFolderEntity(final LevelFolder folder);

    default CreateLevelProfileInput toCreateLevelProfileInput(UUID teacherId, CreateLevelProfileRequest request) {
        final var folders = request.folders() == null ? List.<CreateLevelFolderInput>of() : request.folders().stream()
                .map(this::toCreateLevelFolderInput)
                .toList();
        return new CreateLevelProfileInput(
                teacherId,
                request.name(),
                request.code(),
                request.icon(),
                request.description(),
                folders
        );
    }

    CreateLevelFolderInput toCreateLevelFolderInput(CreateLevelFolderRequest request);

    default UpdateLevelProfileInput toUpdateLevelProfileInput(UUID id, UUID teacherId, UpdateLevelProfileRequest request) {
        final var folders = request.folders() == null ? List.<CreateLevelFolderInput>of() : request.folders().stream()
                .map(this::toCreateLevelFolderInput)
                .toList();
        return new UpdateLevelProfileInput(
                id,
                teacherId,
                request.name(),
                request.icon(),
                request.description(),
                folders
        );
    }

    GetLevelProfileResponse toGetLevelProfileResponse(GetLevelProfileOutput output);

    ListLevelProfilesResponse toListLevelProfilesResponse(ListLevelProfilesOutput output);

    List<LevelFolderResponse> toFolderResponses(List<LevelFolderOutput> folders);
}
