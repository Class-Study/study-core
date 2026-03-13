package com.example.studycore.application.usecase.activity;

import com.example.studycore.application.mapper.ActivityOutputMapper;
import com.example.studycore.application.usecase.activity.output.ListActivitiesOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.ActivityGateway;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.StudentGateway;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListActivitiesByFolderUseCase {

    private static final ActivityOutputMapper MAPPER = ActivityOutputMapper.INSTANCE;

    private final ActivityGateway activityGateway;
    private final FolderGateway folderGateway;
    private final StudentGateway studentGateway;

    public ListActivitiesOutput execute(UUID folderId, UUID authenticatedUserId, boolean teacher) {
        final var folder = folderGateway.findById(folderId)
                .orElseThrow(() -> new NotFoundException("Pasta não encontrada."));

        if (teacher) {
            final var student = studentGateway.findById(folder.getStudentId())
                    .orElseThrow(() -> new NotFoundException("Aluno da pasta não encontrado."));
            if (!authenticatedUserId.equals(student.getTeacherId())) {
                throw new BusinessException("Sem acesso a activities desta pasta.");
            }
        } else if (!authenticatedUserId.equals(folder.getStudentId())) {
            throw new BusinessException("Aluno sem acesso a activities desta pasta.");
        }

        return MAPPER.toListActivitiesOutput(activityGateway.findByFolderId(folderId));
    }
}

