package com.example.studycore.application.usecase.student;

import com.example.studycore.application.usecase.student.output.GetMyStatsOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.infrastructure.persistence.activity.ActivityRepository;
import com.example.studycore.infrastructure.persistence.folder.FolderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetMyStatsUseCase {

    private final StudentGateway studentGateway;
    private final FolderRepository folderRepository;
    private final ActivityRepository activityRepository;

    public GetMyStatsOutput execute(UUID studentId) {
        log.debug("Iniciando GetMyStats | studentId={}", studentId);

        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        // Validar se o aluno está bloqueado
        if (UserStatus.BLOCKED == student.getStatus()) {
            log.warn("✗ GetMyStats | studentId={} | status=BLOCKED", studentId);
            throw new BusinessException("Sua conta foi bloqueada. Entre em contato com o professor.");
        }

        final var folders = folderRepository.findByStudentIdOrderByPositionAsc(studentId);

        int activitiesTotal = 0;
        int activitiesCompleted = 0;

        for (final var folder : folders) {
            final var activities = activityRepository.findByFolderIdOrderByCreatedAtAsc(folder.getId());
            activitiesTotal += activities.size();

            if (folder.getName() != null && folder.getName().toUpperCase().contains("DONE")) {
                activitiesCompleted += activities.size();
            }
        }

        final int classDaysPerWeek = student.getClassDays() == null ? 0 : student.getClassDays().size();
        final int classesThisMonth = classDaysPerWeek * 4;
        final int classesTotal = classesThisMonth * 2;

        final double activitiesProgress = activitiesCompleted * 100.0 / Math.max(activitiesTotal, 1);
        final double classesProgress = classesThisMonth * 100.0 / Math.max(classesTotal, 1);
        final int overallProgress = (int) Math.round((activitiesProgress + classesProgress) / 2.0);

        final var output = new GetMyStatsOutput(
                activitiesCompleted,
                activitiesTotal,
                classesThisMonth,
                classesTotal,
                overallProgress
        );

        log.info("✓ GetMyStats | studentId={} | activitiesCompleted={} | activitiesTotal={} | overallProgress={}%",
                studentId, activitiesCompleted, activitiesTotal, overallProgress);

        return output;
    }
}

