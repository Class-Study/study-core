package com.example.studycore.application.usecase.activity;

import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.ActivityGateway;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.StudentGateway;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteActivityUseCase {

    private final ActivityGateway activityGateway;
    private final FolderGateway folderGateway;
    private final StudentGateway studentGateway;

    @Transactional
    public void execute(UUID teacherId, UUID activityId) {
        final var activity = activityGateway.findById(activityId)
                .orElseThrow(() -> new NotFoundException("Activity não encontrada."));

        final var folder = folderGateway.findById(activity.getFolderId())
                .orElseThrow(() -> new NotFoundException("Pasta da activity não encontrada."));

        final var student = studentGateway.findById(folder.getStudentId())
                .orElseThrow(() -> new NotFoundException("Aluno da pasta não encontrado."));

        if (!teacherId.equals(student.getTeacherId())) {
            throw new BusinessException("Activity não pertence a aluno do professor autenticado.");
        }

        activityGateway.delete(activityId);
    }
}

