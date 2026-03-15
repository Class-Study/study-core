package com.example.studycore.application.usecase.folder;

import com.example.studycore.application.mapper.FolderOutputMapper;
import com.example.studycore.application.mapper.ActivityOutputMapper;
import com.example.studycore.application.usecase.folder.output.WorkspaceOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Activity;
import com.example.studycore.domain.model.Folder;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.ActivityGateway;
import com.example.studycore.domain.port.StudentGateway;

import java.util.Comparator;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetStudentWorkspaceUseCase {

    private static final FolderOutputMapper FOLDER_MAPPER = FolderOutputMapper.INSTANCE;
    private static final ActivityOutputMapper ACTIVITY_MAPPER = ActivityOutputMapper.INSTANCE;

    private final StudentGateway studentGateway;
    private final FolderGateway folderGateway;
    private final ActivityGateway activityGateway;

    public WorkspaceOutput execute(UUID studentId, UUID teacherId) {
        final var student = studentGateway.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Aluno não encontrado."));

        if (!student.getTeacherId().equals(teacherId)) {
            throw new BusinessException("Aluno não pertence ao professor autenticado.");
        }

        final var folders = folderGateway.findByStudentId(studentId).stream()
                .sorted(Comparator.comparing(Folder::getPosition))
                .map(folder -> {
                    log.info("Buscando activities para folder: {} ({})", folder.getName(), folder.getId());
                    final var activities = activityGateway.findByFolderId(folder.getId());
                    log.info("Folder {} ({}) → {} activities encontradas",
                        folder.getName(), folder.getId(), activities.size());
                    for (Activity activity : activities) {
                        log.info("  - Activity: {} ({})", activity.getTitle(), activity.getId());
                    }

                    final var activityOutputs = activities.stream()
                            .sorted(Comparator.comparing(Activity::getCreatedAt))
                            .map(a -> {
                                log.info("Convertendo activity {} para output", a.getTitle());
                                return ACTIVITY_MAPPER.toOutput(a);
                            })
                            .toList();

                    log.info("Folder {} → convertidas {} activities para output",
                        folder.getName(), activityOutputs.size());
                    return FOLDER_MAPPER.toWorkspaceFolderOutput(folder, activityOutputs);
                })
                .toList();

        return new WorkspaceOutput(studentId, folders);
    }
}


