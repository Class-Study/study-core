package com.example.studycore.application.usecase.student;

import com.example.studycore.application.mapper.StudentStatsOutputMapper;
import com.example.studycore.application.usecase.student.output.StudentStatsOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.infrastructure.persistence.activity.ActivityRepository;
import com.example.studycore.infrastructure.persistence.folder.FolderRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetStudentStatsUseCase {

    private static final StudentStatsOutputMapper MAPPER = StudentStatsOutputMapper.INSTANCE;

    private final StudentGateway studentGateway;
    private final FolderRepository folderRepository;
    private final ActivityRepository activityRepository;

    public StudentStatsOutput execute(UUID teacherId, UUID studentId) {
        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        if (!teacherId.equals(student.getTeacherId())) {
            throw new BusinessException("Aluno não pertence ao professor autenticado.");
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

        return MAPPER.toOutput(
                activitiesCompleted,
                activitiesTotal,
                classesThisMonth,
                classesTotal,
                overallProgress
        );
    }
}

