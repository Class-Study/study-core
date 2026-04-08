package com.example.studycore.application.usecase.student;

import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput;
import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput.ActivityItem;
import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput.FolderItem;
import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput.StudyMaterialItem;
import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput.SubfolderItem;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.ActivityGateway;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.domain.port.StudentSubfolderGateway;
import com.example.studycore.domain.port.StudyMaterialGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetStudentWorkspaceFoldersUseCase {

    private final StudentGateway studentGateway;
    private final FolderGateway folderGateway;
    private final StudentSubfolderGateway studentSubfolderGateway;
    private final ActivityGateway activityGateway;
    private final StudyMaterialGateway studyMaterialGateway;

    public GetStudentWorkspaceFoldersOutput execute(UUID studentId) {
        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        if (UserStatus.BLOCKED == student.getStatus()) {
            throw new BusinessException("Sua conta foi bloqueada. Entre em contato com o professor.");
        }

        final var folders = folderGateway.findByStudentId(studentId);

        final List<FolderItem> folderItems = folders.stream().map(folder -> {

            // Student subfolders
            final var subfolderItems = studentSubfolderGateway.findByFolderId(folder.getId()).stream()
                    .map(sub -> {
                        // Exercises in this subfolder
                        final var exercises = activityGateway.findBySubfolderId(sub.getId()).stream()
                                .filter(a -> "EXERCISE".equals(a.getType()))
                                .map(a -> new ActivityItem(a.getId(), sub.getId(), a.getTitle(), a.getType(), a.getConvertedHtml(), a.getCreatedAt(), a.getUpdatedAt()))
                                .toList();

                        // Study materials from the corresponding level subfolder (VIDEO/LINK/DOCUMENT)
                        final List<StudyMaterialItem> studyMaterials = sub.getLevelSubfolderId() != null
                                ? studyMaterialGateway.findBySubfolderId(sub.getLevelSubfolderId()).stream()
                                    .map(m -> new StudyMaterialItem(
                                            m.getId(), m.getLevelFolderId(), m.getSubfolderId(),
                                            m.getTitle(), m.getType().name(), m.getUrl(),
                                            m.getConvertedHtml(), m.getOriginalFilename(),
                                            m.getDescription(), m.getCreatedBy(),
                                            m.getCreatedAt(), m.getUpdatedAt()))
                                    .toList()
                                : List.of();

                        return new SubfolderItem(sub.getId(), sub.getName(), sub.getPosition(),
                                exercises, studyMaterials, sub.getCreatedAt(), sub.getUpdatedAt());
                    })
                    .toList();

            return new FolderItem(folder.getId(), folder.getName(), folder.getPosition(),
                    0, subfolderItems);
        }).toList();

        return new GetStudentWorkspaceFoldersOutput(folderItems, student.getCreatedAt());
    }
}
