package com.example.studycore.application.usecase.student;

import com.example.studycore.application.mapper.StudentOutputMapper;
import com.example.studycore.application.usecase.student.output.GetStudentOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.StudentGateway;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetStudentByIdUseCase {

    private static final StudentOutputMapper MAPPER = StudentOutputMapper.INSTANCE;

    private final StudentGateway studentGateway;

    public GetStudentOutput execute(UUID studentId, UUID teacherId) {
        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + studentId));

        if (!teacherId.equals(student.getTeacherId())) {
            throw new BusinessException("Student does not belong to authenticated teacher.");
        }

        return MAPPER.toGetStudentOutput(student, null);
    }
}

