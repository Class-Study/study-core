package com.example.studycore.application.usecase.studymaterial;

import com.example.studycore.application.mapper.StudyMaterialOutputMapper;
import com.example.studycore.application.usecase.studymaterial.input.CreateStudyMaterialInput;
import com.example.studycore.application.usecase.studymaterial.output.StudyMaterialOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Activity;
import com.example.studycore.domain.model.StudyMaterial;
import com.example.studycore.domain.model.enums.StudyMaterialType;
import com.example.studycore.domain.port.ActivityGateway;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.LevelSubfolderGateway;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.domain.port.StudyMaterialGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateStudyMaterialUseCase {

    private static final StudyMaterialOutputMapper MAPPER = StudyMaterialOutputMapper.INSTANCE;

    private final StudyMaterialGateway studyMaterialGateway;
    private final LevelProfileGateway levelProfileGateway;
    private final LevelSubfolderGateway levelSubfolderGateway;
    private final StudentGateway studentGateway;
    private final FolderGateway folderGateway;
    private final ActivityGateway activityGateway;

    @Transactional
    public StudyMaterialOutput execute(CreateStudyMaterialInput input) {
        final var levelProfile = levelProfileGateway.findById(input.levelProfileId())
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!levelProfile.isSystem() && !input.createdBy().equals(levelProfile.getCreatedBy())) {
            throw new BusinessException("Perfil de nível não pertence ao professor autenticado.");
        }

        final var levelFolder = levelProfile.getFolders().stream()
                .filter(f -> f.getId().equals(input.levelFolderId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Pasta de nível não encontrada neste perfil."));

        // Validate subfolder exists and belongs to the folder
        levelSubfolderGateway.findByIdAndLevelFolderId(input.subfolderId(), input.levelFolderId())
                .orElseThrow(() -> new NotFoundException("Subpasta não encontrada nesta pasta de nível."));

        final var material = StudyMaterial.create(
                input.levelFolderId(),
                input.subfolderId(),
                input.title(),
                input.type(),
                input.url(),
                input.convertedHtml(),
                input.originalFilename(),
                input.description(),
                input.createdBy()
        );

        final var saved = studyMaterialGateway.save(material);

        if (Boolean.TRUE.equals(input.propagateToStudents())
                && input.type() == StudyMaterialType.DOCUMENT) {
            propagateToStudents(input, material, levelFolder.getPosition());
        }

        return MAPPER.toOutput(saved);
    }

    private void propagateToStudents(CreateStudyMaterialInput input, StudyMaterial material, Integer levelFolderPosition) {
        final var students = studentGateway.findByLevelProfileId(input.levelProfileId());

        for (final var student : students) {
            try {
                final var studentFolders = folderGateway.findByStudentId(student.getId());
                final var correspondingFolder = studentFolders.stream()
                        .filter(folder -> folder.getPosition().equals(levelFolderPosition))
                        .findFirst();

                if (correspondingFolder.isPresent()) {
                    final var activity = Activity.createWithSubfolder(
                            correspondingFolder.get().getId(),
                            material.getSubfolderId(),
                            material.getTitle(),
                            "MATERIAL",
                            material.getConvertedHtml(),
                            input.createdBy()
                    );
                    activityGateway.save(activity);
                }
            } catch (Exception e) {
                System.err.println("Erro ao propagar material para aluno " + student.getId() + ": " + e.getMessage());
            }
        }
    }
}
