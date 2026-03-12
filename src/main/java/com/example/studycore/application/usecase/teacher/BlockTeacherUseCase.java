package com.example.studycore.application.usecase.teacher;

import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.port.TeacherGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BlockTeacherUseCase {

    private final TeacherGateway teacherGateway;

    @Transactional
    public void execute(final UUID teacherId) {
        // Verificar se o professor existe e é realmente um professor
        final var teacher = teacherGateway.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("Teacher not found with id: " + teacherId));

        if (teacher.getRole() != UserRole.TEACHER) {
            throw new NotFoundException("Teacher not found with id: " + teacherId);
        }

        // Bloquear professor (DELETE lógico)
        teacherGateway.block(teacherId);
    }
}
