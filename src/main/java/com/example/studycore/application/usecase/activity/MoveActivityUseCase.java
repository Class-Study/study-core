package com.example.studycore.application.usecase.activity;

import com.example.studycore.application.mapper.ActivityOutputMapper;
import com.example.studycore.application.usecase.activity.input.MoveActivityInput;
import com.example.studycore.application.usecase.activity.output.GetActivityOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Activity;
import com.example.studycore.domain.port.ActivityGateway;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.StudentGateway;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MoveActivityUseCase {

    private static final ActivityOutputMapper MAPPER = ActivityOutputMapper.INSTANCE;

    private final ActivityGateway activityGateway;
    private final FolderGateway folderGateway;
    private final StudentGateway studentGateway;

    @Transactional
    public GetActivityOutput execute(MoveActivityInput input) {
        // Validar se a atividade existe
        final var activity = activityGateway.findById(input.activityId())
                .orElseThrow(() -> new NotFoundException("Atividade não encontrada."));

        // Validar se a pasta de origem existe
        final var currentFolder = folderGateway.findById(activity.getFolderId())
                .orElseThrow(() -> new NotFoundException("Pasta de origem da atividade não encontrada."));

        // Validar se a pasta de destino existe
        final var targetFolder = folderGateway.findById(input.targetFolderId())
                .orElseThrow(() -> new NotFoundException("Pasta de destino não encontrada."));

        // Validar se o aluno existe
        final var student = studentGateway.findById(currentFolder.getStudentId())
                .orElseThrow(() -> new NotFoundException("Aluno da pasta não encontrado."));

        // Validar permissão: professor responsável OU o próprio aluno
        // Se o usuário autenticado é igual ao studentId, é o próprio aluno movendo suas atividades
        final var isStudent = input.teacherId().equals(currentFolder.getStudentId());
        // Se o usuário autenticado é professor, deve ser responsável pelo aluno
        final var isTeacherResponsible = input.teacherId().equals(student.getTeacherId());

        if (!isStudent && !isTeacherResponsible) {
            throw new BusinessException("Você não tem permissão para mover atividades deste aluno.");
        }

        // Validar consistência: ambas as pastas devem pertencer ao mesmo aluno
        if (!currentFolder.getStudentId().equals(targetFolder.getStudentId())) {
            throw new BusinessException("Não é possível mover atividades entre alunos diferentes.");
        }

        // Criar uma nova instância da atividade com o novo folderId
        final var movedActivity = Activity.with(
                activity.getId(),
                input.targetFolderId(),
                activity.getTitle(),
                activity.getType(),
                activity.getConvertedHtml(),
                activity.getCreatedBy(),
                activity.getCreatedAt(),
                OffsetDateTime.now()
        );

        // Persistir a mudança
        final var saved = activityGateway.save(movedActivity);

        return MAPPER.toGetActivityOutput(saved);
    }
}

