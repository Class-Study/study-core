package com.example.studycore.application.usecase.folder;

import com.example.studycore.application.mapper.FolderOutputMapper;
import com.example.studycore.application.usecase.folder.input.CreateFolderInput;
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
public class CreateFolderUseCase {

    private static final FolderOutputMapper MAPPER = FolderOutputMapper.INSTANCE;

    private final FolderGateway folderGateway;
    private final StudentGateway studentGateway;

    @Transactional
    public GetFolderOutput execute(CreateFolderInput input) {
        final var student = studentGateway.findById(input.studentId())
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        if (!input.teacherId().equals(student.getTeacherId())) {
            throw new BusinessException("Aluno não pertence ao professor autenticado.");
        }

        final var folder = Folder.create(
                input.studentId(),
                input.name(),
                input.position()
        );

        return MAPPER.toGetFolderOutput(folderGateway.save(folder));
    }
}
