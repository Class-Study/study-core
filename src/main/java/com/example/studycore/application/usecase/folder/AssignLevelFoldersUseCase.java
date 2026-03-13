package com.example.studycore.application.usecase.folder;

import com.example.studycore.application.mapper.FolderOutputMapper;
import com.example.studycore.application.usecase.folder.input.AssignLevelFoldersInput;
import com.example.studycore.application.usecase.folder.output.ListFoldersOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Folder;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.StudentGateway;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssignLevelFoldersUseCase {

    private static final FolderOutputMapper MAPPER = FolderOutputMapper.INSTANCE;

    private final FolderGateway folderGateway;
    private final StudentGateway studentGateway;
    private final LevelProfileGateway levelProfileGateway;

    @Transactional
    public ListFoldersOutput execute(AssignLevelFoldersInput input) {
        final var student = studentGateway.findById(input.studentId())
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        if (!input.teacherId().equals(student.getTeacherId())) {
            throw new BusinessException("Aluno não pertence ao professor autenticado.");
        }

        if (student.getLevelProfileId() == null) {
            throw new BusinessException("Aluno não possui perfil de nível vinculado.");
        }

        final var levelProfile = levelProfileGateway.findById(student.getLevelProfileId())
                .orElseThrow(() -> new NotFoundException("Perfil de nível do aluno não encontrado."));

        final var selected = levelProfile.getFolders().stream()
                .filter(f -> input.levelFolderIds().contains(f.getId()))
                .toList();

        if (selected.size() != input.levelFolderIds().size()) {
            throw new BusinessException("Um ou mais level_folders não pertencem ao perfil do aluno.");
        }

        final List<Folder> foldersToCreate = selected.stream()
                .map(levelFolder -> Folder.create(
                        input.studentId(),
                        levelFolder.getName(),
                        levelFolder.getPosition()
                ))
                .toList();

        return MAPPER.toListFoldersOutput(folderGateway.saveAll(foldersToCreate));
    }
}
