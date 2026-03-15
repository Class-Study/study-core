package com.example.studycore.application.usecase.activity;

import com.example.studycore.application.mapper.ActivityOutputMapper;
import com.example.studycore.application.usecase.activity.input.UpdateActivityInput;
import com.example.studycore.application.usecase.activity.output.GetActivityOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Activity;
import com.example.studycore.domain.port.ActivityGateway;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.StudentGateway;
import java.time.OffsetDateTime;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateActivityUseCase {

    private static final ActivityOutputMapper MAPPER = ActivityOutputMapper.INSTANCE;
    private static final Set<String> ALLOWED_TYPES = Set.of("EXERCISE", "WORKSPACE");

    private final ActivityGateway activityGateway;
    private final FolderGateway folderGateway;
    private final StudentGateway studentGateway;

    @Transactional
    public GetActivityOutput execute(UpdateActivityInput input) {
        final var existing = activityGateway.findById(input.activityId())
                .orElseThrow(() -> new NotFoundException("Activity não encontrada."));

        final var folder = folderGateway.findById(existing.getFolderId())
                .orElseThrow(() -> new NotFoundException("Pasta da activity não encontrada."));

        final var student = studentGateway.findById(folder.getStudentId())
                .orElseThrow(() -> new NotFoundException("Aluno da pasta não encontrado."));

        if (!input.teacherId().equals(student.getTeacherId())) {
            throw new BusinessException("Activity não pertence a aluno do professor autenticado.");
        }

        final var type = input.type() != null ? input.type().trim().toUpperCase() : existing.getType();
        if (!ALLOWED_TYPES.contains(type)) {
            throw new BusinessException("Tipo de atividade inválido. Use EXERCISE ou WORKSPACE.");
        }

        final var updated = Activity.with(
                existing.getId(),
                existing.getFolderId(),
                input.title() != null ? input.title().trim() : existing.getTitle(),
                type,
                existing.getConvertedHtml(),
                existing.getCreatedBy(),
                existing.getCreatedAt(),
                OffsetDateTime.now()
        );

        return MAPPER.toGetActivityOutput(activityGateway.save(updated));
    }
}

