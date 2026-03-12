package com.example.studycore.application.mapper;

import com.example.studycore.application.usecase.teacher.output.GetTeacherOutput;
import com.example.studycore.application.usecase.teacher.output.ListTeachersOutput;
import com.example.studycore.domain.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TeacherOutputMapper {

    TeacherOutputMapper INSTANCE = Mappers.getMapper(TeacherOutputMapper.class);

    @Mapping(target = "role", expression = "java(teacher.getRole().name())")
    @Mapping(target = "status", expression = "java(teacher.getStatus().name())")
    GetTeacherOutput toGetTeacherOutput(User teacher);

    default ListTeachersOutput toListTeachersOutput(List<User> teachers) {
        if (teachers == null) return new ListTeachersOutput(List.of());

        final var teacherSummaries = teachers.stream()
                .map(this::toTeacherSummary)
                .toList();

        return new ListTeachersOutput(teacherSummaries);
    }

    @Mapping(target = "status", expression = "java(teacher.getStatus().name())")
    ListTeachersOutput.TeacherSummary toTeacherSummary(User teacher);
}

