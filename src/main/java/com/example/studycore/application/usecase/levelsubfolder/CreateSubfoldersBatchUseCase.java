package com.example.studycore.application.usecase.levelsubfolder;

import com.example.studycore.application.usecase.levelfoldertemplate.output.LevelFolderTemplateOutput;
import com.example.studycore.application.usecase.levelsubfolder.input.BatchExerciseInput;
import com.example.studycore.application.usecase.levelsubfolder.input.BatchMaterialInput;
import com.example.studycore.application.usecase.levelsubfolder.input.BatchSubfolderInput;
import com.example.studycore.application.usecase.levelsubfolder.input.CreateSubfoldersBatchInput;
import com.example.studycore.application.usecase.levelsubfolder.output.BatchSubfolderOutput;
import com.example.studycore.application.usecase.levelsubfolder.output.CreateSubfoldersBatchOutput;
import com.example.studycore.application.usecase.studymaterial.output.StudyMaterialOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Activity;
import com.example.studycore.domain.model.LevelFolderTemplate;
import com.example.studycore.domain.model.LevelSubfolder;
import com.example.studycore.domain.model.StudentSubfolder;
import com.example.studycore.domain.model.StudyMaterial;
import com.example.studycore.domain.model.enums.StudyMaterialType;
import com.example.studycore.domain.port.ActivityGateway;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.LevelFolderTemplateGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.LevelSubfolderGateway;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.domain.port.StudentSubfolderGateway;
import com.example.studycore.domain.port.StudyMaterialGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class CreateSubfoldersBatchUseCase {

    private final LevelSubfolderGateway levelSubfolderGateway;
    private final LevelProfileGateway levelProfileGateway;
    private final LevelFolderTemplateGateway levelFolderTemplateGateway;
    private final StudyMaterialGateway studyMaterialGateway;
    private final StudentGateway studentGateway;
    private final FolderGateway folderGateway;
    private final ActivityGateway activityGateway;
    private final StudentSubfolderGateway studentSubfolderGateway;

    @Transactional
    public void execute(CreateSubfoldersBatchInput input) {
        final var levelProfile = levelProfileGateway.findById(input.levelProfileId())
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!levelProfile.isSystem() && !input.createdBy().equals(levelProfile.getCreatedBy())) {
            throw new BusinessException("Perfil de nível não pertence ao professor autenticado.");
        }

        final var levelFolder = levelProfile.getFolders().stream()
                .filter(f -> f.getId().equals(input.levelFolderId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Pasta de nível não encontrada neste perfil."));

        final int basePosition = levelSubfolderGateway.countByLevelFolderId(input.levelFolderId());
        final var results = new ArrayList<BatchSubfolderOutput>();
        final var subfolderList = input.subfolders() != null ? input.subfolders() : List.<BatchSubfolderInput>of();

        for (int i = 0; i < subfolderList.size(); i++) {
            final var item = subfolderList.get(i);
            final var position = item.position() != null ? item.position() : basePosition + i + 1;

            final var subfolder = LevelSubfolder.create(
                    input.levelFolderId(),
                    item.name(),
                    position,
                    input.createdBy()
            );
            final var savedSubfolder = levelSubfolderGateway.save(subfolder);

            // Create exercises (LevelFolderTemplate) linked to this subfolder
            final var savedExercises = new ArrayList<LevelFolderTemplateOutput>();
            final List<BatchExerciseInput> exercises = item.exercises() != null ? item.exercises() : List.of();
            for (final var ex : exercises) {
                final var template = LevelFolderTemplate.create(
                        input.levelFolderId(),
                        savedSubfolder.getId(),
                        ex.title(),
                        ex.type() != null ? ex.type() : "EXERCISE",
                        ex.originalFilename(),
                        ex.convertedHtml(),
                        input.createdBy()
                );
                final var saved = levelFolderTemplateGateway.save(template);
                savedExercises.add(new LevelFolderTemplateOutput(
                        saved.getId(), saved.getLevelFolderId(), saved.getSubfolderId(),
                        saved.getTitle(), saved.getType(), saved.getOriginalFilename(),
                        saved.getConvertedHtml(), saved.getCreatedAt()
                ));
            }

            // Create study materials linked to this subfolder
            final var savedMaterials = new ArrayList<StudyMaterialOutput>();
            final List<BatchMaterialInput> materials = item.materials() != null ? item.materials() : List.of();
            for (final var mat : materials) {
                final StudyMaterialType matType;
                try {
                    matType = StudyMaterialType.valueOf(mat.type().toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new BusinessException("Tipo de material inválido: " + mat.type());
                }

                final var material = StudyMaterial.create(
                        input.levelFolderId(),
                        savedSubfolder.getId(),
                        mat.title(),
                        matType,
                        mat.url(),
                        mat.convertedHtml(),
                        mat.originalFilename(),
                        mat.description(),
                        input.createdBy()
                );
                final var saved = studyMaterialGateway.save(material);
                savedMaterials.add(new StudyMaterialOutput(
                        saved.getId(), saved.getLevelFolderId(), saved.getSubfolderId(),
                        saved.getSubfolderType() != null ? saved.getSubfolderType().name() : null,
                        saved.getTitle(), saved.getType().name(), saved.getUrl(),
                        saved.getConvertedHtml(), saved.getOriginalFilename(),
                        saved.getDescription(), saved.getCreatedBy(),
                        saved.getCreatedAt(), saved.getUpdatedAt()
                ));
            }

            results.add(new BatchSubfolderOutput(
                    savedSubfolder.getId(),
                    savedSubfolder.getLevelFolderId(),
                    savedSubfolder.getName(),
                    savedSubfolder.getPosition(),
                    savedExercises,
                    savedMaterials,
                    savedSubfolder.getCreatedAt(),
                    savedSubfolder.getUpdatedAt()
            ));
        }

        // Propagation — side effect, error per student is logged but does not block response
        for (int i = 0; i < subfolderList.size(); i++) {
            if (Boolean.TRUE.equals(subfolderList.get(i).propagateToStudents())) {
                propagateToStudents(input, results.get(i), levelFolder.getPosition());
            }
        }
    }

    private void propagateToStudents(CreateSubfoldersBatchInput input, BatchSubfolderOutput created, Integer levelFolderPosition) {
        final var students = studentGateway.findByLevelProfileId(input.levelProfileId());

        for (final var student : students) {
            try {
                final var studentFolders = folderGateway.findByStudentId(student.getId());
                final var correspondingFolder = studentFolders.stream()
                        .filter(f -> f.getPosition().equals(levelFolderPosition))
                        .findFirst();

                if (correspondingFolder.isEmpty()) {
                    log.warn("No matching student folder (position={}) for student {}", levelFolderPosition, student.getId());
                    continue;
                }

                final var studentSub = StudentSubfolder.create(
                        correspondingFolder.get().getId(),
                        created.id(),
                        created.name(),
                        created.position()
                );
                final var savedStudentSub = studentSubfolderGateway.save(studentSub);

                // Propagate exercises
                for (final var ex : created.exercises()) {
                    activityGateway.save(Activity.createWithSubfolder(
                            correspondingFolder.get().getId(),
                            savedStudentSub.getId(),
                            ex.title(),
                            "EXERCISE",
                            ex.convertedHtml(),
                            input.createdBy()
                    ));
                }

                // Propagate DOCUMENT materials only (VIDEO/LINK are external — no activity copy needed)
                for (final var mat : created.materials()) {
                    if ("DOCUMENT".equals(mat.type())) {
                        activityGateway.save(Activity.createWithSubfolder(
                                correspondingFolder.get().getId(),
                                savedStudentSub.getId(),
                                mat.title(),
                                "MATERIAL",
                                mat.convertedHtml(),
                                input.createdBy()
                        ));
                    }
                }

            } catch (Exception e) {
                log.error("Erro ao propagar batch para aluno {}: {}", student.getId(), e.getMessage());
            }
        }
    }
}



