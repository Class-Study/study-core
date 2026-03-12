package com.example.studycore.application.usecase.teacher;

import com.example.studycore.application.mapper.TeacherOutputMapper;
import com.example.studycore.application.usecase.teacher.input.UpdateTeacherInput;
import com.example.studycore.application.usecase.teacher.output.GetTeacherOutput;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.User;
import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.port.TeacherGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateTeacherUseCase {

    private static final TeacherOutputMapper MAPPER = TeacherOutputMapper.INSTANCE;

    private final TeacherGateway teacherGateway;

    @Transactional
    public GetTeacherOutput execute(final UpdateTeacherInput input) {
        // Buscar professor existente
        final var existingTeacher = teacherGateway.findById(input.id())
                .orElseThrow(() -> new NotFoundException("Teacher not found with id: " + input.id()));

        // Validar que é realmente um professor
        if (existingTeacher.getRole() != UserRole.TEACHER) {
            throw new NotFoundException("Teacher not found with id: " + input.id());
        }

        // Criar professor atualizado (mantendo campos que não podem ser alterados)
        final var updatedTeacher = User.with(
                existingTeacher.getId(),
                input.name() != null ? input.name().trim() : existingTeacher.getName(),
                existingTeacher.getEmail(), // E-mail não pode ser alterado
                existingTeacher.getPasswordHash(), // Password não pode ser alterado
                existingTeacher.getRole(), // Role não pode ser alterado
                existingTeacher.getStatus(), // Status não pode ser alterado via update
                input.avatarUrl() != null ? input.avatarUrl() : existingTeacher.getAvatarUrl(),
                input.phone() != null ? input.phone() : existingTeacher.getPhone(),
                existingTeacher.getLastSeenAt(),
                existingTeacher.getCreatedAt()
        );

        // Salvar alterações
        final var savedTeacher = teacherGateway.save(updatedTeacher);

        return MAPPER.toGetTeacherOutput(savedTeacher);
    }
}

