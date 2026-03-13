package com.example.studycore.application.usecase.student;

import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.StudentGateway;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockStudentUseCase {

    private final StudentGateway studentGateway;

    @Transactional
    public void execute(UUID studentId, UUID teacherId) {
        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + studentId));

        if (!teacherId.equals(student.getTeacherId())) {
            throw new BusinessException("Student does not belong to authenticated teacher.");
        }

        studentGateway.block(studentId);
    }
}

