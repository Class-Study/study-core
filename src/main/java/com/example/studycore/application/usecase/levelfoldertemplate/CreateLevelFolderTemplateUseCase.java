package com.example.studycore.application.usecase.levelfoldertemplate;

import com.example.studycore.application.mapper.LevelFolderTemplateOutputMapper;
import com.example.studycore.application.usecase.levelfoldertemplate.input.CreateLevelFolderTemplateInput;
import com.example.studycore.application.usecase.levelfoldertemplate.output.LevelFolderTemplateOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Activity;
import com.example.studycore.domain.model.LevelFolderTemplate;
import com.example.studycore.domain.port.ActivityGateway;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.LevelFolderTemplateGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.StudentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateLevelFolderTemplateUseCase {

    private static final LevelFolderTemplateOutputMapper MAPPER = LevelFolderTemplateOutputMapper.INSTANCE;

    private final LevelFolderTemplateGateway levelFolderTemplateGateway;
    private final LevelProfileGateway levelProfileGateway;
    private final StudentGateway studentGateway;
    private final FolderGateway folderGateway;
    private final ActivityGateway activityGateway;

    @Transactional
    public LevelFolderTemplateOutput execute(CreateLevelFolderTemplateInput input) {
        final var levelProfile = levelProfileGateway.findById(input.levelProfileId())
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!levelProfile.isSystem() && !input.teacherId().equals(levelProfile.getCreatedBy())) {
            throw new BusinessException("Perfil de nível não pertence ao professor autenticado.");
        }

        final var levelFolder = levelProfile.getFolders().stream()
                .filter(f -> f.getId().equals(input.levelFolderId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Pasta de nível não encontrada neste perfil."));

        final var template = LevelFolderTemplate.create(
                input.levelFolderId(),
                input.title(),
                input.type(),
                input.originalFilename(),
                input.convertedHtml(),
                input.teacherId()
        );

        final var savedTemplate = levelFolderTemplateGateway.save(template);

        // Propagação automática para alunos se habilitada
        if (Boolean.TRUE.equals(input.propagateToStudents())) {
            propagateToStudents(input, template, levelFolder.getPosition());
        }

        return MAPPER.toOutput(savedTemplate);
    }

    private void propagateToStudents(CreateLevelFolderTemplateInput input, LevelFolderTemplate template, Integer levelFolderPosition) {
        // Buscar todos os alunos com este levelProfileId
        final var studentsWithProfile = studentGateway.findByLevelProfileId(input.levelProfileId());

        for (final var student : studentsWithProfile) {
            try {
                // Buscar todas as pastas do aluno
                final var studentFolders = folderGateway.findByStudentId(student.getId());

                // Encontrar a pasta do aluno que corresponde à LevelFolder (mesma position)
                final var correspondingFolder = studentFolders.stream()
                        .filter(folder -> folder.getPosition().equals(levelFolderPosition))
                        .findFirst();

                if (correspondingFolder.isPresent()) {
                    // Criar Activity como cópia do template
                    final var activity = Activity.create(
                            correspondingFolder.get().getId(),
                            template.getTitle(),
                            template.getType(),
                            template.getConvertedHtml(),
                            input.teacherId()
                    );

                    activityGateway.save(activity);
                }
            } catch (Exception e) {
                // Log do erro mas não falha toda a propagação
                // Em produção, considerar usar um logger apropriado
                System.err.println("Erro ao propagar template para aluno " + student.getId() + ": " + e.getMessage());
            }
        }
    }
}

