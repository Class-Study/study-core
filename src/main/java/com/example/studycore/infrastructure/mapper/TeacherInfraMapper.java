package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.teacher.input.CreateTeacherInput;
import com.example.studycore.application.usecase.teacher.input.UpdateTeacherInput;
import com.example.studycore.application.usecase.teacher.output.GetTeacherConfigOutput;
import com.example.studycore.application.usecase.teacher.output.GetTeacherOutput;
import com.example.studycore.application.usecase.teacher.output.ListTeachersOutput;
import com.example.studycore.domain.model.User;
import com.example.studycore.domain.model.enums.ThemePreference;
import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.infrastructure.api.controllers.teacher.request.CreateTeacherRequest;
import com.example.studycore.infrastructure.api.controllers.teacher.request.UpdateTeacherRequest;
import com.example.studycore.infrastructure.api.controllers.teacher.response.GetTeacherConfigResponse;
import com.example.studycore.infrastructure.api.controllers.teacher.response.GetTeacherResponse;
import com.example.studycore.infrastructure.api.controllers.teacher.response.ListTeachersResponse;
import com.example.studycore.infrastructure.persistence.auth.UserEntity;
import com.example.studycore.infrastructure.persistence.teacher.TeacherEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper
public interface TeacherInfraMapper {

    TeacherInfraMapper INSTANCE = Mappers.getMapper(TeacherInfraMapper.class);

    // UserEntity + TeacherEntity → User domain (mesmo padrão do StudentInfraMapper)
    default User fromUserAndTeacherEntity(UserEntity user, TeacherEntity teacher) {
        if (user == null) return null;
        return User.with(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                UserRole.valueOf(user.getRole()),
                UserStatus.valueOf(user.getStatus()),
                user.getAvatarUrl(),
                user.getPhone(),
                user.getPreferenceTheme() != null
                        ? ThemePreference.valueOf(user.getPreferenceTheme().toUpperCase())
                        : ThemePreference.LIGHT,
                user.getLastSeenAt(),
                teacher != null ? teacher.getPixKey() : null,
                teacher != null ? teacher.getPixKeyType() : null,
                user.getCreatedAt()
        );
    }

    // User domain → UserEntity (apenas campos da tabela users)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "passwordHash", source = "passwordHash")
    @Mapping(target = "role", expression = "java(teacher.getRole().name())")
    @Mapping(target = "status", expression = "java(teacher.getStatus().name())")
    @Mapping(target = "preferenceTheme", expression = "java(teacher.getPreferenceTheme().name().toLowerCase())")
    @Mapping(target = "avatarUrl", source = "avatarUrl")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "lastSeenAt", source = "lastSeenAt")
    @Mapping(target = "createdAt", source = "createdAt")
    UserEntity toUserEntity(User teacher);

    // User domain → TeacherEntity (apenas campos da tabela teachers)
    default TeacherEntity toTeacherEntity(User teacher) {
        if (teacher == null) return null;
        final var entity = new TeacherEntity();
        entity.setId(teacher.getId());
        entity.setPixKey(teacher.getPixKey());
        return entity;
    }

    CreateTeacherInput toCreateTeacherInput(CreateTeacherRequest request);

    UpdateTeacherInput toUpdateTeacherInput(UUID id, UpdateTeacherRequest request);

    GetTeacherResponse toGetTeacherResponse(GetTeacherOutput output);

    ListTeachersResponse toListTeachersResponse(ListTeachersOutput output);

    @Mapping(target = "startTimeMorning",   source = "startTimeMorning")
    @Mapping(target = "endTimeMorning",     source = "endTimeMorning")
    @Mapping(target = "startTimeAfternoon", source = "startTimeAfternoon")
    @Mapping(target = "endTimeAfternoon",   source = "endTimeAfternoon")
    GetTeacherConfigResponse.WorkHourResponse toWorkHourResponse(GetTeacherConfigOutput output);

    @Mapping(target = "pixKey",   source = "pixKey")
    @Mapping(target = "pixKeyType",   source = "pixKeyType")
    @Mapping(target = "workHour", expression = "java(toWorkHourResponse(output))")
    GetTeacherConfigResponse toGetTeacherConfigResponse(GetTeacherConfigOutput output);
}
