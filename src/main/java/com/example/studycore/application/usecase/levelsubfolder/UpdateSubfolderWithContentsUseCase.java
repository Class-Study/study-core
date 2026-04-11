package com.example.studycore.application.usecase.levelsubfolder;

import com.example.studycore.application.usecase.levelsubfolder.input.ExerciseItemInput;
import com.example.studycore.application.usecase.levelsubfolder.input.MaterialItemInput;
import com.example.studycore.application.usecase.levelsubfolder.input.UpdateSubfolderWithContentsInput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Activity;
import com.example.studycore.domain.model.LevelFolderTemplate;
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
public class UpdateSubfolderWithContentsUseCase {

    private final LevelSubfolderGateway levelSubfolderGateway;
    private final LevelProfileGateway levelProfileGateway;
    private final LevelFolderTemplateGateway levelFolderTemplateGateway;
    private final StudyMaterialGateway studyMaterialGateway;
    private final StudentGateway studentGateway;
    private final FolderGateway folderGateway;
    private final ActivityGateway activityGateway;
    private final StudentSubfolderGateway studentSubfolderGateway;

    @Transactional
    public void execute(UpdateSubfolderWithContentsInput input) {

        // ── 1. Validate profile / folder / subfolder ──────────────────────
        final var levelProfile = levelProfileGateway.findById(input.levelProfileId())
                .orElseThrow(() -> new NotFoundException("Perfil de nível não encontrado."));

        if (!levelProfile.isSystem() && !input.updatedBy().equals(levelProfile.getCreatedBy())) {
            throw new BusinessException("Perfil de nível não pertence ao professor autenticado.");
        }

        final var levelFolder = levelProfile.getFolders().stream()
                .filter(f -> f.getId().equals(input.levelFolderId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Pasta de nível não encontrada neste perfil."));

        final var subfolder = levelSubfolderGateway
                .findByIdAndLevelFolderId(input.subfolderId(), input.levelFolderId())
                .orElseThrow(() -> new NotFoundException("Subpasta não encontrada nesta pasta de nível."));

        // ── 2. Rename subfolder ───────────────────────────────────────────
        levelSubfolderGateway.save(subfolder.update(input.name(), subfolder.getPosition()));

        // ── 3. Delete exercises ───────────────────────────────────────────
        if (input.deletedExerciseIds() != null) {
            input.deletedExerciseIds().forEach(levelFolderTemplateGateway::delete);
        }

        // ── 4. Create new exercises ───────────────────────────────────────
        final var newTemplates = new ArrayList<LevelFolderTemplate>();
        final List<ExerciseItemInput> exercises = input.exercises() != null ? input.exercises() : List.of();
        for (final var ex : exercises) {
            if (ex.id() != null) continue; // existing — keep as-is

            if (ex.title() == null || ex.title().isBlank()) {
                throw new BusinessException("Título do exercício é obrigatório para novos itens.");
            }

            final var template = LevelFolderTemplate.create(
                    input.levelFolderId(),
                    input.subfolderId(),
                    ex.title(),
                    ex.type() != null ? ex.type() : "EXERCISE",
                    ex.originalFilename(),
                    ex.convertedHtml(),
                    input.updatedBy()
            );
            newTemplates.add(levelFolderTemplateGateway.save(template));
        }

        // ── 5. Delete materials ───────────────────────────────────────────
        if (input.deletedMaterialIds() != null) {
            input.deletedMaterialIds().forEach(studyMaterialGateway::delete);
        }

        // ── 6. Create new materials ───────────────────────────────────────
        final var newMaterials = new ArrayList<StudyMaterial>();
        final List<MaterialItemInput> materials = input.materials() != null ? input.materials() : List.of();
        for (final var mat : materials) {
            if (mat.id() != null) continue; // existing — keep as-is

            if (mat.title() == null || mat.title().isBlank()) {
                throw new BusinessException("Título do material é obrigatório para novos itens.");
            }
            if (mat.type() == null || mat.type().isBlank()) {
                throw new BusinessException("Tipo do material é obrigatório para novos itens.");
            }

            final StudyMaterialType matType;
            try {
                matType = StudyMaterialType.valueOf(mat.type().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Tipo de material inválido: " + mat.type());
            }

            final var material = StudyMaterial.create(
                    input.levelFolderId(),
                    input.subfolderId(),
                    mat.title(),
                    matType,
                    mat.url(),
                    mat.convertedHtml(),
                    mat.originalFilename(),
                    mat.description(),
                    input.updatedBy()
            );
            newMaterials.add(studyMaterialGateway.save(material));
        }

        // ── 7. Propagate only new items to students ───────────────────────
        if (Boolean.TRUE.equals(input.propagateToStudents())
                && (!newTemplates.isEmpty() || !newMaterials.isEmpty())) {
            propagateNewItemsToStudents(input, newTemplates, newMaterials, levelFolder.getPosition());
        }
    }

    // ── private helpers ───────────────────────────────────────────────────────

    private void propagateNewItemsToStudents(
            UpdateSubfolderWithContentsInput input,
            List<LevelFolderTemplate> newTemplates,
            List<StudyMaterial> newMaterials,
            Integer levelFolderPosition
    ) {
        final var students = studentGateway.findByLevelProfileId(input.levelProfileId());

        for (final var student : students) {
            try {
                final var studentFolders = folderGateway.findByStudentId(student.getId());
                final var correspondingFolder = studentFolders.stream()
                        .filter(f -> f.getPosition().equals(levelFolderPosition))
                        .findFirst();

                if (correspondingFolder.isEmpty()) continue;

                final var studentSubOpt = studentSubfolderGateway
                        .findByFolderIdAndLevelSubfolderId(
                                correspondingFolder.get().getId(), input.subfolderId());

                // Student has no copy of this subfolder yet — skip (propagation only appends)
                if (studentSubOpt.isEmpty()) continue;

                final var studentSubfolderId = studentSubOpt.get().getId();

                for (final var template : newTemplates) {
                    activityGateway.save(Activity.createWithSubfolder(
                            correspondingFolder.get().getId(),
                            studentSubfolderId,
                            template.getTitle(),
                            template.getType(),
                            "",
                            template.getConvertedHtml(),
                            input.updatedBy()
                    ));
                }

                // Only DOCUMENT materials produce an activity in the student workspace
                for (final var mat : newMaterials) {

                    activityGateway.save(Activity.createWithSubfolder(
                            correspondingFolder.get().getId(),
                            studentSubfolderId,
                            mat.getTitle(),
                            mat.getType().name(),
                            mat.getUrl(),
                            mat.getConvertedHtml(),
                            input.updatedBy()
                    ));

                }

            } catch (Exception e) {
                log.error("Erro ao propagar atualização de subpasta para aluno {}: {}",
                        student.getId(), e.getMessage());
            }
        }
    }
}


