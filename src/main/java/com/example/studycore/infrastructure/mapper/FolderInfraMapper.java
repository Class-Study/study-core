package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.folder.input.AssignLevelFoldersInput;
import com.example.studycore.application.usecase.folder.input.CreateFolderInput;
import com.example.studycore.application.usecase.folder.input.UpdateFolderInput;
import com.example.studycore.application.usecase.folder.output.GetFolderOutput;
import com.example.studycore.application.usecase.folder.output.ListFoldersOutput;
import com.example.studycore.domain.model.Folder;
import com.example.studycore.infrastructure.api.controllers.folder.request.AssignLevelFoldersRequest;
import com.example.studycore.infrastructure.api.controllers.folder.request.CreateFolderRequest;
import com.example.studycore.infrastructure.api.controllers.folder.request.UpdateFolderRequest;
import com.example.studycore.infrastructure.api.controllers.folder.response.GetFolderResponse;
import com.example.studycore.infrastructure.api.controllers.folder.response.ListFoldersResponse;
import com.example.studycore.infrastructure.persistence.folder.FolderEntity;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FolderInfraMapper {

    FolderInfraMapper INSTANCE = Mappers.getMapper(FolderInfraMapper.class);

    default Folder fromEntity(FolderEntity entity) {
        if (entity == null) return null;
        return Folder.with(entity.getId(), entity.getStudentId(), entity.getName(), entity.getPosition(), entity.getCreatedAt());
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "studentId", source = "studentId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "position", source = "position")
    @Mapping(target = "createdAt", source = "createdAt")
    FolderEntity toEntity(Folder folder);

    default CreateFolderInput toCreateFolderInput(UUID teacherId, UUID studentId, CreateFolderRequest request) {
        return new CreateFolderInput(teacherId, studentId, request.name(), request.position());
    }

    default AssignLevelFoldersInput toAssignLevelFoldersInput(UUID teacherId, UUID studentId, AssignLevelFoldersRequest request) {
        return new AssignLevelFoldersInput(teacherId, studentId, request.levelFolderIds());
    }

    default UpdateFolderInput toUpdateFolderInput(UUID teacherId, UUID folderId, UpdateFolderRequest request) {
        return new UpdateFolderInput(teacherId, folderId, request.name(), request.position());
    }

    GetFolderResponse toGetFolderResponse(GetFolderOutput output);

    ListFoldersResponse toListFoldersResponse(ListFoldersOutput output);
}

