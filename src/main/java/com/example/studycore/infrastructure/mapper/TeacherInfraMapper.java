package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.teacher.input.CreateTeacherInput;
import com.example.studycore.application.usecase.teacher.input.UpdateTeacherInput;
import com.example.studycore.application.usecase.teacher.output.GetTeacherOutput;
import com.example.studycore.application.usecase.teacher.output.ListTeachersOutput;
import com.example.studycore.domain.model.User;
import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.infrastructure.api.controllers.teacher.request.CreateTeacherRequest;
import com.example.studycore.infrastructure.api.controllers.teacher.request.UpdateTeacherRequest;
import com.example.studycore.infrastructure.api.controllers.teacher.response.GetTeacherResponse;
import com.example.studycore.infrastructure.api.controllers.teacher.response.ListTeachersResponse;
import com.example.studycore.infrastructure.persistence.auth.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface TeacherInfraMapper {

    TeacherInfraMapper INSTANCE = Mappers.getMapper(TeacherInfraMapper.class);

    // Entity → Domain (reutiliza User.with)
    default User fromEntity(final UserEntity entity) {
        if (entity == null) return null;
        return User.with(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getPasswordHash(),
                UserRole.valueOf(entity.getRole()),
                UserStatus.valueOf(entity.getStatus()),
                entity.getAvatarUrl(),
                entity.getPhone(),
                entity.getLastSeenAt(),
                entity.getCreatedAt()
        );
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "passwordHash", source = "passwordHash")
    @Mapping(target = "role", expression = "java(teacher.getRole().name())")
    @Mapping(target = "status", expression = "java(teacher.getStatus().name())")
    @Mapping(target = "avatarUrl", source = "avatarUrl")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "lastSeenAt", source = "lastSeenAt")
    @Mapping(target = "createdAt", source = "createdAt")
    UserEntity toEntity(final User teacher);

    // Request → Input
    CreateTeacherInput toCreateTeacherInput(final CreateTeacherRequest request);

    default UpdateTeacherInput toUpdateTeacherInput(final UUID id, final UpdateTeacherRequest request) {
        return new UpdateTeacherInput(id, request.name(), request.phone(), request.avatarUrl());
    }

    // Output → Response
    GetTeacherResponse toGetTeacherResponse(final GetTeacherOutput output);

    ListTeachersResponse toListTeachersResponse(final ListTeachersOutput output);
}

