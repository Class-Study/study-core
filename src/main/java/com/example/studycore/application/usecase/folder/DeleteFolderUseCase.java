package com.example.studycore.application.usecase.folder;

import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.StudentGateway;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteFolderUseCase {

    private final FolderGateway folderGateway;
    private final StudentGateway studentGateway;

    @Transactional
    public void execute(UUID teacherId, UUID folderId) {
        final var folder = folderGateway.findById(folderId)
                .orElseThrow(() -> new NotFoundException("Pasta não encontrada."));

        final var student = studentGateway.findById(folder.getStudentId())
                .orElseThrow(() -> new NotFoundException("Aluno da pasta não encontrado."));

        if (!teacherId.equals(student.getTeacherId())) {
            throw new BusinessException("Pasta não pertence a um aluno do professor autenticado.");
        }

        folderGateway.delete(folderId);
    }
}

