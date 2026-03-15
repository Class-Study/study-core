package com.example.studycore.application.usecase.student;

import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.StudentGateway;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnblockStudentUseCase {

    private final StudentGateway studentGateway;

    public void execute(final UUID studentId, final UUID teacherId) {
        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        if (!student.getTeacherId().equals(teacherId)) {
            throw new BusinessException("Acesso negado.");
        }

        if (student.getStatus() != UserStatus.BLOCKED) {
            throw new BusinessException("Aluno não está bloqueado.");
        }

        studentGateway.unblock(studentId);
    }
}

