package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.StudentNote;
import com.example.studycore.domain.port.StudentNoteGateway;
import com.example.studycore.infrastructure.mapper.StudentNoteInfraMapper;
import com.example.studycore.infrastructure.persistence.studentnote.StudentNoteRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StudentNoteGatewayImpl implements StudentNoteGateway {

    private static final StudentNoteInfraMapper STUDENT_NOTE_INFRA_MAPPER = StudentNoteInfraMapper.INSTANCE;

    private final StudentNoteRepository studentNoteRepository;

    @Override
    public List<StudentNote> findAllByStudentId(UUID studentId) {
        return studentNoteRepository.findByStudentIdOrderByCreatedAtDesc(studentId).stream()
                .map(STUDENT_NOTE_INFRA_MAPPER::fromEntity)
                .toList();
    }

    @Override
    public StudentNote save(StudentNote note) {
        final var saved = studentNoteRepository.save(STUDENT_NOTE_INFRA_MAPPER.toEntity(note));
        return STUDENT_NOTE_INFRA_MAPPER.fromEntity(saved);
    }
}

