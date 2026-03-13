package com.example.studycore.application.usecase.student;

import com.example.studycore.application.mapper.StudentOutputMapper;
import com.example.studycore.application.usecase.student.output.ListStudentsOutput;
import com.example.studycore.domain.port.StudentGateway;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListStudentsUseCase {

    private static final StudentOutputMapper MAPPER = StudentOutputMapper.INSTANCE;

    private final StudentGateway studentGateway;

    public ListStudentsOutput execute(UUID teacherId) {
        return MAPPER.toListStudentsOutput(studentGateway.findAllByTeacherId(teacherId));
    }
}

