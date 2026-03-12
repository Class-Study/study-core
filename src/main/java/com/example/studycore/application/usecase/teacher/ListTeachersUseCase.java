package com.example.studycore.application.usecase.teacher;

import com.example.studycore.application.mapper.TeacherOutputMapper;
import com.example.studycore.application.usecase.teacher.output.ListTeachersOutput;
import com.example.studycore.domain.port.TeacherGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListTeachersUseCase {

    private static final TeacherOutputMapper MAPPER = TeacherOutputMapper.INSTANCE;

    private final TeacherGateway teacherGateway;

    public ListTeachersOutput execute() {
        final var teachers = teacherGateway.findAll();
        return MAPPER.toListTeachersOutput(teachers);
    }
}
