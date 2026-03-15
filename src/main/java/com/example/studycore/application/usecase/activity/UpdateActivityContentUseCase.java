package com.example.studycore.application.usecase.activity;

import com.example.studycore.application.mapper.ActivityOutputMapper;
import com.example.studycore.application.usecase.activity.input.UpdateActivityContentInput;
import com.example.studycore.application.usecase.activity.output.GetActivityOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Activity;
import com.example.studycore.domain.port.ActivityGateway;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.StudentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class UpdateActivityContentUseCase {

    private static final ActivityOutputMapper MAPPER = ActivityOutputMapper.INSTANCE;

    private final ActivityGateway activityGateway;
    private final FolderGateway folderGateway;
    private final StudentGateway studentGateway;

    @Transactional
    public GetActivityOutput execute(UpdateActivityContentInput input) {
        final var existing = activityGateway.findById(input.activityId())
                .orElseThrow(() -> new NotFoundException("Activity não encontrada."));

        final var folder = folderGateway.findById(existing.getFolderId())
                .orElseThrow(() -> new NotFoundException("Pasta da activity não encontrada."));

        if (input.teacher()) {
            final var student = studentGateway.findById(folder.getStudentId())
                    .orElseThrow(() -> new NotFoundException("Aluno da pasta não encontrado."));
            if (!input.authenticatedUserId().equals(student.getTeacherId())) {
                throw new BusinessException("Sem acesso para editar conteúdo da activity.");
            }
        } else if (!input.authenticatedUserId().equals(folder.getStudentId())) {
            throw new BusinessException("Aluno sem acesso para editar conteúdo da activity.");
        }

        final var updated = Activity.with(
                existing.getId(),
                existing.getFolderId(),
                existing.getTitle(),
                existing.getType(),
                input.convertedHtml() == null ? "" : input.convertedHtml(),
                existing.getCreatedBy(),
                existing.getCreatedAt(),
                OffsetDateTime.now()
        );

        return MAPPER.toGetActivityOutput(activityGateway.save(updated));
    }
}
