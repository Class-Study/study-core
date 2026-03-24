package com.example.studycore.infrastructure.mapper;

import com.example.studycore.application.usecase.activity.output.StudentActivityOutput;
import com.example.studycore.application.usecase.student.input.CreateStudentInput;
import com.example.studycore.application.usecase.student.input.UpdateStudentInput;
import com.example.studycore.application.usecase.student.output.GetStudentOutput;
import com.example.studycore.application.usecase.student.output.ListStudentsOutput;
import com.example.studycore.application.usecase.student.output.StudentStatsOutput;
import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.infrastructure.api.controllers.activity.response.StudentActivityResponse;
import com.example.studycore.infrastructure.api.controllers.student.request.CreateStudentRequest;
import com.example.studycore.infrastructure.api.controllers.student.request.UpdateStudentRequest;
import com.example.studycore.infrastructure.api.controllers.student.response.GetStudentResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.ListStudentsResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.StudentStatsResponse;
import com.example.studycore.infrastructure.persistence.auth.UserEntity;
import com.example.studycore.infrastructure.persistence.student.StudentEntity;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StudentInfraMapper {

    StudentInfraMapper INSTANCE = Mappers.getMapper(StudentInfraMapper.class);

    default Student fromUserAndStudentEntity(UserEntity user, StudentEntity student) {
        if (user == null) return null;
        return Student.with(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                UserRole.valueOf(user.getRole()),
                UserStatus.valueOf(user.getStatus()),
                user.getAvatarUrl(),
                user.getPhone(),
                student != null ? student.getTeacherId() : null,
                student != null ? student.getLevelProfileId() : null,
                student != null && student.getClassDays() != null ? Arrays.asList(student.getClassDays()) : List.of(),
                student != null ? student.getClassTime() : null,
                student != null ? student.getClassDuration() : null,
                student != null ? student.getClassRate() : null,
                student != null ? student.getMeetPlatform() : null,
                student != null ? student.getMeetLink() : null,
                student != null ? student.getStartDate() : null,
                student != null ? student.getNotesPrivate() : null,
                student != null ? student.getCreatedAt() : user.getCreatedAt()
        );
    }

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "passwordHash", source = "passwordHash")
    @Mapping(target = "role", expression = "java(student.getRole().name())")
    @Mapping(target = "status", expression = "java(student.getStatus().name())")
    @Mapping(target = "preferenceTheme", ignore = true)
    @Mapping(target = "avatarUrl", source = "avatarUrl")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "lastSeenAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    UserEntity toUserEntity(Student student);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "teacherId", source = "teacherId")
    @Mapping(target = "levelProfileId", source = "levelProfileId")
    @Mapping(target = "classDays", expression = "java(student.getClassDays() != null ? student.getClassDays().toArray(String[]::new) : new String[]{})")
    @Mapping(target = "classTime", source = "classTime")
    @Mapping(target = "classDuration", source = "classDuration")
    @Mapping(target = "classRate", source = "classRate")
    @Mapping(target = "meetPlatform", source = "meetPlatform")
    @Mapping(target = "meetLink", source = "meetLink")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "notesPrivate", source = "notesPrivate")
    @Mapping(target = "createdAt", source = "createdAt")
    StudentEntity toStudentEntity(Student student);

    default CreateStudentInput toCreateStudentInput(UUID teacherId, CreateStudentRequest request) {
        return new CreateStudentInput(
                teacherId,
                request.name(),
                request.email(),
                request.phone(),
                request.avatarUrl(),
                request.levelProfileId(),
                request.classTime(),
                request.classDays(),
                request.classDuration(),
                request.classRate(),
                request.meetPlatform(),
                request.meetLink(),
                request.startDate()
        );
    }

    default UpdateStudentInput toUpdateStudentInput(UUID id, UUID teacherId, UpdateStudentRequest request) {
        return new UpdateStudentInput(
                id,
                teacherId,
                request.name(),
                request.phone(),
                request.avatarUrl(),
                request.levelProfileId(),
                request.classTime(),
                request.classDays(),
                request.classDuration(),
                request.classRate(),
                request.meetPlatform(),
                request.meetLink(),
                request.startDate() // novo campo
        );
    }

    GetStudentResponse toGetStudentResponse(GetStudentOutput output);

    ListStudentsResponse toListStudentsResponse(ListStudentsOutput output);

    StudentActivityResponse toStudentActivityResponse(StudentActivityOutput output);

    StudentStatsResponse toStudentStatsResponse(StudentStatsOutput output);
}
