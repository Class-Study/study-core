package com.example.studycore.application.usecase.studentnote;

import com.example.studycore.application.mapper.StudentNoteOutputMapper;
import com.example.studycore.application.usecase.studentnote.input.CreateStudentNoteInput;
import com.example.studycore.application.usecase.studentnote.output.StudentNoteOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.StudentNote;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.domain.port.StudentNoteGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateStudentNoteUseCase {

    private static final StudentNoteOutputMapper MAPPER = StudentNoteOutputMapper.INSTANCE;

    private final StudentGateway studentGateway;
    private final StudentNoteGateway studentNoteGateway;

    public StudentNoteOutput execute(CreateStudentNoteInput input) {
        final var student = studentGateway.findById(input.studentId())
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        if (!input.teacherId().equals(student.getTeacherId())) {
            throw new BusinessException("Aluno não pertence ao professor autenticado.");
        }

        final var note = StudentNote.create(
                input.studentId(),
                input.authorId(),
                input.type(),
                input.content()
        );

        return MAPPER.toOutput(studentNoteGateway.save(note));
    }
}

