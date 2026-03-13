package com.example.studycore.application.usecase.folder;

import com.example.studycore.application.mapper.FolderOutputMapper;
import com.example.studycore.application.usecase.folder.output.ListFoldersOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.StudentGateway;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListFoldersByStudentUseCase {

    private static final FolderOutputMapper MAPPER = FolderOutputMapper.INSTANCE;

    private final FolderGateway folderGateway;
    private final StudentGateway studentGateway;

    public ListFoldersOutput execute(UUID teacherId, UUID studentId) {
        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        if (!teacherId.equals(student.getTeacherId())) {
            throw new BusinessException("Aluno não pertence ao professor autenticado.");
        }

        return MAPPER.toListFoldersOutput(folderGateway.findByStudentId(studentId));
    }
}

