package com.example.studycore.application.usecase.activity;

import com.example.studycore.application.usecase.activity.output.GetMyActivitiesOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.infrastructure.persistence.activity.ActivityRepository;
import com.example.studycore.infrastructure.persistence.folder.FolderRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetMyActivitiesUseCase {

    private final StudentGateway studentGateway;
    private final FolderRepository folderRepository;
    private final ActivityRepository activityRepository;

    public GetMyActivitiesOutput execute(UUID studentId) {
        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        // Validar se o aluno está bloqueado
        if (UserStatus.BLOCKED == student.getStatus()) {
            throw new BusinessException("Sua conta foi bloqueada. Entre em contato com o professor.");
        }

        // Buscar todas as pastas do aluno ordenadas por position
        final var folders = folderRepository.findByStudentIdOrderByPositionAsc(studentId);

        if (folders.isEmpty()) {
            return new GetMyActivitiesOutput(List.of());
        }

        // Para cada pasta, buscar as atividades ordenadas por createdAt
        final List<GetMyActivitiesOutput.FolderWithActivities> foldersWithActivities = new ArrayList<>();

        for (final var folder : folders) {
            final var activities = activityRepository.findByFolderIdOrderByCreatedAtAsc(folder.getId());

            final var activityItems = activities.stream()
                    .map(activity -> new GetMyActivitiesOutput.ActivityItem(
                            activity.getId(),
                            activity.getTitle(),
                            activity.getType(),
                            activity.getConvertedHtml(),
                            activity.getCreatedBy(),
                            activity.getCreatedAt(),
                            activity.getUpdatedAt()
                    ))
                    .toList();

            foldersWithActivities.add(new GetMyActivitiesOutput.FolderWithActivities(
                    folder.getId(),
                    folder.getName(),
                    folder.getPosition(),
                    activityItems
            ));
        }

        return new GetMyActivitiesOutput(foldersWithActivities);
    }
}


