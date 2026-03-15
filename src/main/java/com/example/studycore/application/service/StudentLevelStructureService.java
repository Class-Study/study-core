package com.example.studycore.application.service;

import com.example.studycore.domain.model.Activity;
import com.example.studycore.domain.model.Folder;
import com.example.studycore.domain.port.ActivityGateway;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.LevelFolderTemplateGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.UUID;

/**
 * Serviço de Domínio para gerenciar a estrutura de pastas e atividades de um aluno
 * com base no seu LevelProfile.
 *
 * Responsável por:
 * - Criar pastas e atividades a partir de um nível (CreateStudentUseCase, UpdateStudentUseCase)
 * - Remover pastas e atividades antigas quando o nível muda
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class StudentLevelStructureService {

    private final FolderGateway folderGateway;
    private final ActivityGateway activityGateway;
    private final LevelFolderTemplateGateway levelFolderTemplateGateway;
    private final LevelProfileGateway levelProfileGateway;

    /**
     * Cria a estrutura completa de pastas e atividades para um aluno com base no LevelProfile.
     *
     * @param studentId      ID do aluno
     * @param levelProfileId ID do perfil de nível
     * @param teacherId      ID do professor (proprietário)
     */
    public void createFoldersAndActivitiesFromLevelProfile(
            UUID studentId,
            UUID levelProfileId,
            UUID teacherId
    ) {
        if (levelProfileId == null) {
            log.info("No level profile selected for student {}", studentId);
            return;
        }

        final var levelProfile = levelProfileGateway.findById(levelProfileId);

        if (levelProfile.isEmpty()) {
            log.warn("Level profile {} not found for student {}", levelProfileId, studentId);
            return;
        }

        final var folders = levelProfile.get().getFolders();

        if (folders == null || folders.isEmpty()) {
            log.info("Level profile {} has no folders", levelProfileId);
            return;
        }

        log.info("Creating {} folders and their activities for student {} from level profile {}",
                folders.size(), studentId, levelProfileId);

        folders.stream()
                .sorted(Comparator.comparing(com.example.studycore.domain.model.LevelFolder::getPosition))
                .forEach(levelFolder -> {
                    // 1. Criar a folder real para o aluno
                    final var folder = Folder.create(
                            studentId,
                            levelFolder.getName(),
                            levelFolder.getPosition()
                    );
                    final var savedFolder = folderGateway.save(folder);
                    log.debug("Created folder {} for student {}", levelFolder.getName(), studentId);

                    // 2. Buscar templates dessa level_folder
                    final var templates = levelFolderTemplateGateway.findAllByFolderId(levelFolder.getId());

                    if (templates != null && !templates.isEmpty()) {
                        log.debug("Found {} templates for folder {}, creating activities for student {}",
                                templates.size(), levelFolder.getName(), studentId);

                        // 3. Para cada template, criar uma activity real
                        templates.forEach(template -> {
                            final var activity = Activity.create(
                                    savedFolder.getId(),
                                    template.getTitle(),
                                    template.getType(),
                                    template.getConvertedHtml(),
                                    teacherId
                            );
                            activityGateway.save(activity);
                            log.debug("Created activity {} from template for folder {}",
                                    template.getTitle(), levelFolder.getName());
                        });
                    } else {
                        log.debug("No templates found for folder {}", levelFolder.getName());
                    }
                });

        log.info("Folders and activities created successfully for student {}", studentId);
    }

    /**
     * Remove toda a estrutura de pastas e atividades de um aluno (Hard Delete).
     *
     * IMPORTANTE: A ordem é crítica devido às constraints de chave estrangeira:
     * 1. Delete todas as Activities de cada Folder
     * 2. Delete a Folder em si
     *
     * @param studentId ID do aluno
     */
    public void deleteAllFoldersAndActivities(UUID studentId) {
        log.info("Deleting all folders and activities for student {}", studentId);

        // Buscar todas as pastas do aluno
        final var oldFolders = folderGateway.findByStudentId(studentId);

        if (oldFolders == null || oldFolders.isEmpty()) {
            log.info("No folders found for student {}", studentId);
            return;
        }

        log.debug("Found {} folders to delete for student {}", oldFolders.size(), studentId);

        // Para cada pasta: deletar atividades primeiro, depois a pasta
        for (final var folder : oldFolders) {
            try {
                // 1. Deletar todas as atividades da pasta (respeitando chave estrangeira)
                final var activities = activityGateway.findByFolderId(folder.getId());

                if (activities != null && !activities.isEmpty()) {
                    log.debug("Deleting {} activities from folder {}", activities.size(), folder.getId());
                    activities.forEach(activity -> {
                        activityGateway.delete(activity.getId());
                        log.debug("Deleted activity {} from folder {}", activity.getId(), folder.getId());
                    });
                }

                // 2. Deletar a pasta
                folderGateway.delete(folder.getId());
                log.debug("Deleted folder {} for student {}", folder.getId(), studentId);

            } catch (Exception e) {
                log.error("Error deleting folder {} for student {}: {}", folder.getId(), studentId, e.getMessage(), e);
                throw new RuntimeException("Failed to delete folder structure for student: " + studentId, e);
            }
        }

        log.info("Successfully deleted all folders and activities for student {}", studentId);
    }
}

