package com.example.studycore.application.usecase.teacher;

import com.example.studycore.application.mapper.TeacherOutputMapper;
import com.example.studycore.application.usecase.teacher.output.GetTeacherOutput;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.port.TeacherGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetTeacherByIdUseCase {

    private static final TeacherOutputMapper MAPPER = TeacherOutputMapper.INSTANCE;

    private final TeacherGateway teacherGateway;

    public GetTeacherOutput execute(final UUID teacherId) {
        final var teacher = teacherGateway.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("Teacher not found with id: " + teacherId));

        // Validar que é realmente um professor
        if (teacher.getRole() != UserRole.TEACHER) {
            throw new NotFoundException("Teacher not found with id: " + teacherId);
        }

        return MAPPER.toGetTeacherOutput(teacher, null);
    }
}
