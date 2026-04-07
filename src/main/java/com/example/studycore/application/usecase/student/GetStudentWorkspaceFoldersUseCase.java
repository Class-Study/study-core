package com.example.studycore.application.usecase.student;

import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput;
import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput.LevelFolderItem;
import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput.LevelSubfolderItem;
import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput.StudyMaterialItem;
import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput.TemplateItem;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.LevelFolderTemplateGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.LevelSubfolderGateway;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.domain.port.StudyMaterialGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetStudentWorkspaceFoldersUseCase {

    private final StudentGateway studentGateway;
    private final LevelProfileGateway levelProfileGateway;
    private final LevelSubfolderGateway levelSubfolderGateway;
    private final LevelFolderTemplateGateway levelFolderTemplateGateway;
    private final StudyMaterialGateway studyMaterialGateway;

    public GetStudentWorkspaceFoldersOutput execute(UUID studentId) {
        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        if (UserStatus.BLOCKED == student.getStatus()) {
            throw new BusinessException("Sua conta foi bloqueada. Entre em contato com o professor.");
        }

        if (student.getLevelProfileId() == null) {
            return new GetStudentWorkspaceFoldersOutput(List.of(), student.getCreatedAt());
        }

        final var levelProfile = levelProfileGateway.findById(student.getLevelProfileId())
                .orElseThrow(() -> new NotFoundException("Perfil de nível do aluno não encontrado."));

        final List<LevelFolderItem> folderItems = levelProfile.getFolders().stream().map(folder -> {

            final var rootTemplates = levelFolderTemplateGateway
                    .findRootByFolderId(folder.getId())
                    .stream()
                    .map(t -> new TemplateItem(
                            t.getId(),
                            t.getLevelFolderId(),
                            t.getSubfolderId(),
                            t.getTitle(),
                            t.getType(),
                            t.getOriginalFilename(),
                            t.getConvertedHtml(),
                            t.getCreatedAt()
                    ))
                    .toList();

            final var subfolders = levelSubfolderGateway
                    .findByLevelFolderId(folder.getId())
                    .stream()
                    .map(sub -> {
                        final var subTemplates = levelFolderTemplateGateway
                                .findBySubfolderId(sub.getId())
                                .stream()
                                .map(t -> new TemplateItem(
                                        t.getId(),
                                        t.getLevelFolderId(),
                                        t.getSubfolderId(),
                                        t.getTitle(),
                                        t.getType(),
                                        t.getOriginalFilename(),
                                        t.getConvertedHtml(),
                                        t.getCreatedAt()
                                ))
                                .toList();

                        final var studyMaterials = studyMaterialGateway
                                .findBySubfolderId(sub.getId())
                                .stream()
                                .map(m -> new StudyMaterialItem(
                                        m.getId(),
                                        m.getLevelFolderId(),
                                        m.getSubfolderId(),
                                        null, // subfolderType reservado
                                        m.getTitle(),
                                        m.getType().name(),
                                        m.getUrl(),
                                        m.getConvertedHtml(),
                                        m.getOriginalFilename(),
                                        m.getDescription(),
                                        m.getCreatedBy(),
                                        m.getCreatedAt(),
                                        m.getUpdatedAt()
                                ))
                                .toList();

                        return new LevelSubfolderItem(
                                sub.getId(),
                                sub.getName(),
                                sub.getPosition(),
                                subTemplates,
                                studyMaterials,
                                sub.getCreatedAt(),
                                sub.getUpdatedAt()
                        );
                    })
                    .toList();

            return new LevelFolderItem(
                    folder.getId(),
                    folder.getName(),
                    folder.getPosition(),
                    folder.getInitialFiles(),
                    rootTemplates,
                    subfolders
            );
        }).toList();

        return new GetStudentWorkspaceFoldersOutput(folderItems, student.getCreatedAt());
    }
}
