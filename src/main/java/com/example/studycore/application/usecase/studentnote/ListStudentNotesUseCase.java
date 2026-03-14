package com.example.studycore.application.usecase.studentnote;

import com.example.studycore.application.mapper.StudentNoteOutputMapper;
import com.example.studycore.application.usecase.studentnote.output.StudentNoteOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.domain.port.StudentNoteGateway;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListStudentNotesUseCase {

    private static final StudentNoteOutputMapper MAPPER = StudentNoteOutputMapper.INSTANCE;

    private final StudentGateway studentGateway;
    private final StudentNoteGateway studentNoteGateway;

    public List<StudentNoteOutput> execute(UUID teacherId, UUID studentId) {
        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        if (!teacherId.equals(student.getTeacherId())) {
            throw new BusinessException("Aluno não pertence ao professor autenticado.");
        }

        return studentNoteGateway.findAllByStudentId(studentId).stream()
                .map(MAPPER::toOutput)
                .toList();
    }
}

