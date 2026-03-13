package com.example.studycore.application.usecase.billing;

import com.example.studycore.application.usecase.billing.input.UpdateStudentRateInput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.port.StudentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateStudentRateUseCase {

    private final StudentGateway studentGateway;

    @Transactional
    public void execute(UpdateStudentRateInput input) {
        final var student = studentGateway.findById(input.studentId())
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        if (!input.teacherId().equals(student.getTeacherId())) {
            throw new BusinessException("Aluno não pertence ao professor autenticado.");
        }

        final Student updated = Student.with(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getPasswordHash(),
                student.getRole(),
                student.getStatus(),
                student.getAvatarUrl(),
                student.getPhone(),
                student.getTeacherId(),
                student.getLevelProfileId(),
                student.getClassDays(),
                student.getClassTime(),
                student.getClassDuration(),
                input.classRate(),
                student.getMeetPlatform(),
                student.getMeetLink(),
                student.getStartDate(),
                student.getNotesPrivate(),
                student.getCreatedAt()
        );

        studentGateway.save(updated);
    }
}

