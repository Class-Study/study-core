package com.example.studycore.application.usecase.studentnote;

import com.example.studycore.application.usecase.studentnote.output.GetMyNotesOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.domain.port.StudentNoteGateway;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetMyNotesUseCase {

    private final StudentGateway studentGateway;
    private final StudentNoteGateway studentNoteGateway;

    public List<GetMyNotesOutput> execute(UUID studentId) {
        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        // Validar se o aluno está bloqueado
        if (UserStatus.BLOCKED == student.getStatus()) {
            throw new BusinessException("Sua conta foi bloqueada. Entre em contato com o professor.");
        }

        // Buscar apenas as notas públicas do aluno
        return studentNoteGateway.findAllByStudentId(studentId).stream()
                .filter(note -> "PUBLIC".equals(note.getType()))
                .map(note -> new GetMyNotesOutput(
                        note.getId(),
                        note.getType(),
                        note.getContent(),
                        note.getCreatedAt()
                ))
                .toList();
    }
}


