package com.example.studycore.application.usecase.activity;

import com.example.studycore.application.mapper.StudentActivityOutputMapper;
import com.example.studycore.application.usecase.activity.output.StudentActivityOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.infrastructure.persistence.activity.ActivityRepository;
import com.example.studycore.infrastructure.persistence.folder.FolderRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListStudentActivitiesUseCase {

    private static final StudentActivityOutputMapper MAPPER = StudentActivityOutputMapper.INSTANCE;

    private final StudentGateway studentGateway;
    private final FolderRepository folderRepository;
    private final ActivityRepository activityRepository;

    public List<StudentActivityOutput> execute(UUID teacherId, UUID studentId) {
        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        if (!teacherId.equals(student.getTeacherId())) {
            throw new BusinessException("Aluno não pertence ao professor autenticado.");
        }

        final var folders = folderRepository.findByStudentIdOrderByPositionAsc(studentId);
        if (folders.isEmpty()) {
            return List.of();
        }

        final List<StudentActivityOutput> result = new ArrayList<>();
        for (final var folder : folders) {
            final var activities = activityRepository.findByFolderIdOrderByCreatedAtAsc(folder.getId());
            for (final var activity : activities) {
                result.add(MAPPER.toOutput(
                        activity.getId(),
                        activity.getTitle(),
                        activity.getType(),
                        folder.getName(),
                        folder.getId(),
                        activity.getCreatedAt()
                ));
            }
        }

        return result.stream()
                .sorted(Comparator.comparing(StudentActivityOutput::createdAt).reversed())
                .toList();
    }
}

