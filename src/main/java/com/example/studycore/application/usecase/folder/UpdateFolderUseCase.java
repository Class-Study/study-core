package com.example.studycore.application.usecase.folder;

import com.example.studycore.application.mapper.FolderOutputMapper;
import com.example.studycore.application.usecase.folder.input.UpdateFolderInput;
import com.example.studycore.application.usecase.folder.output.GetFolderOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Folder;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.StudentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateFolderUseCase {

    private static final FolderOutputMapper MAPPER = FolderOutputMapper.INSTANCE;

    private final FolderGateway folderGateway;
    private final StudentGateway studentGateway;

    @Transactional
    public GetFolderOutput execute(UpdateFolderInput input) {
        final var folder = folderGateway.findById(input.folderId())
                .orElseThrow(() -> new NotFoundException("Pasta não encontrada."));

        final var student = studentGateway.findById(folder.getStudentId())
                .orElseThrow(() -> new NotFoundException("Aluno da pasta não encontrado."));

        if (!input.teacherId().equals(student.getTeacherId())) {
            throw new BusinessException("Pasta não pertence a um aluno do professor autenticado.");
        }

        final var updated = Folder.with(
                folder.getId(),
                folder.getStudentId(),
                input.name() != null ? input.name().trim() : folder.getName(),
                input.position() != null ? input.position() : folder.getPosition(),
                folder.getCreatedAt()
        );

        return MAPPER.toGetFolderOutput(folderGateway.save(updated));
    }
}
