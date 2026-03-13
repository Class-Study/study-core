package com.example.studycore.application.usecase.activity;

import com.example.studycore.application.mapper.ActivityOutputMapper;
import com.example.studycore.application.usecase.activity.output.GetActivityOutput;
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
public class GetActivityByIdUseCase {

    private static final ActivityOutputMapper MAPPER = ActivityOutputMapper.INSTANCE;

    private final ActivityGateway activityGateway;
    private final FolderGateway folderGateway;
    private final StudentGateway studentGateway;

    public GetActivityOutput execute(UUID activityId, UUID authenticatedUserId, boolean teacher) {
        final var activity = activityGateway.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity não encontrada."));

        final var folder = folderGateway.findById(activity.getFolderId())
                .orElseThrow(() -> new NotFoundException("Pasta da activity não encontrada."));

        if (teacher) {
            final var student = studentGateway.findById(folder.getStudentId())
                    .orElseThrow(() -> new NotFoundException("Aluno da pasta não encontrado."));
            if (!authenticatedUserId.equals(student.getTeacherId())) {
                throw new BusinessException("Sem acesso à activity.");
            }
        } else if (!authenticatedUserId.equals(folder.getStudentId())) {
            throw new BusinessException("Aluno sem acesso à activity.");
        }

        return MAPPER.toGetActivityOutput(activity);
    }
}

