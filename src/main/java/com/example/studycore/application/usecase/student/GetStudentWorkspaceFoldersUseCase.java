package com.example.studycore.application.usecase.student;

import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput;
import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput.ActivityItem;
import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput.FolderItem;
import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput.StudyMaterialItem;
import com.example.studycore.application.usecase.student.output.GetStudentWorkspaceFoldersOutput.SubfolderItem;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Activity;
import com.example.studycore.domain.model.Folder;
import com.example.studycore.domain.model.StudentSubfolder;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.ActivityGateway;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.domain.port.StudentSubfolderGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetStudentWorkspaceFoldersUseCase {

    private static final Set<String> EXERCISE_TYPES = Set.of("EXERCISE", "WORKSPACE");
    private static final Set<String> MATERIAL_TYPES = Set.of("MATERIAL", "DOCUMENT", "VIDEO", "LINK");

    private final StudentGateway studentGateway;
    private final FolderGateway folderGateway;
    private final StudentSubfolderGateway studentSubfolderGateway;
    private final ActivityGateway activityGateway;

    public GetStudentWorkspaceFoldersOutput execute(UUID studentId) {
        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        if (UserStatus.BLOCKED == student.getStatus()) {
            throw new BusinessException("Sua conta foi bloqueada. Entre em contato com o professor.");
        }

        final var folders = folderGateway.findByStudentId(studentId);
        final var folderItems = folders.stream().map(this::toFolderItem).toList();

        return new GetStudentWorkspaceFoldersOutput(folderItems, student.getCreatedAt());
    }

    private FolderItem toFolderItem(Folder folder) {
        final var subfolders = studentSubfolderGateway.findByFolderId(folder.getId())
                .stream()
                .map(this::toSubfolderItem)
                .toList();

        return new FolderItem(folder.getId(), folder.getName(), folder.getPosition(), 0, subfolders);
    }

    private SubfolderItem toSubfolderItem(StudentSubfolder sub) {
        final var activities = activityGateway.findBySubfolderId(sub.getId());

        final var exercises = activities.stream()
                .filter(a -> EXERCISE_TYPES.contains(a.getType()))
                .map(a -> toActivityItem(a, sub.getId()))
                .toList();

        final var studyMaterials = activities.stream()
                .filter(a -> MATERIAL_TYPES.contains(a.getType()))
                .map(a -> toStudyMaterialItem(a, sub.getId()))
                .toList();

        return new SubfolderItem(
                sub.getId(), sub.getName(), sub.getPosition(),
                exercises, studyMaterials,
                sub.getCreatedAt(), sub.getUpdatedAt()
        );
    }

    private static ActivityItem toActivityItem(Activity activity, UUID subfolderId) {
        return new ActivityItem(
                activity.getId(), subfolderId, activity.getTitle(),
                activity.getType(), activity.getConvertedHtml(),
                activity.getCreatedAt(), activity.getUpdatedAt()
        );
    }

    private static StudyMaterialItem toStudyMaterialItem(Activity activity, UUID subfolderId) {
        return new StudyMaterialItem(
                activity.getId(),
                null,
                subfolderId,
                activity.getTitle(),
                mapMaterialType(activity.getType()),
                activity.getUrl(),
                activity.getConvertedHtml(),
                null,
                null,
                activity.getCreatedBy(),
                activity.getCreatedAt(),
                activity.getUpdatedAt()
        );
    }

    private static String mapMaterialType(String type) {
        return "MATERIAL".equals(type) ? "DOCUMENT" : type;
    }
}
