package com.example.studycore.application.usecase.activity;

import com.example.studycore.application.mapper.ActivityOutputMapper;
import com.example.studycore.application.usecase.activity.input.CreateActivityInput;
import com.example.studycore.application.usecase.activity.output.GetActivityOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Activity;
import com.example.studycore.domain.port.ActivityGateway;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.StudentGateway;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateActivityUseCase {

    private static final ActivityOutputMapper MAPPER = ActivityOutputMapper.INSTANCE;
    private static final Set<String> ALLOWED_TYPES = Set.of("EXERCISE", "WORKSPACE");

    private final ActivityGateway activityGateway;
    private final FolderGateway folderGateway;
    private final StudentGateway studentGateway;

    @Transactional
    public GetActivityOutput execute(CreateActivityInput input) {
        final var folder = folderGateway.findById(input.folderId())
                .orElseThrow(() -> new NotFoundException("Pasta não encontrada."));

        final var student = studentGateway.findById(folder.getStudentId())
                .orElseThrow(() -> new NotFoundException("Aluno da pasta não encontrado."));

        if (!input.teacherId().equals(student.getTeacherId())) {
            throw new BusinessException("Pasta não pertence a um aluno do professor autenticado.");
        }

        final var type = input.type() == null ? "EXERCISE" : input.type().trim().toUpperCase();
        if (!ALLOWED_TYPES.contains(type)) {
            throw new BusinessException("Tipo de atividade inválido. Use EXERCISE ou WORKSPACE.");
        }

        final var activity = Activity.create(
                folder.getId(),
                input.title(),
                type,
                input.contentHtml(),
                input.teacherId()
        );

        return MAPPER.toGetActivityOutput(activityGateway.save(activity));
    }
}
