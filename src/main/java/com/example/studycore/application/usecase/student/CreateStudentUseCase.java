package com.example.studycore.application.usecase.student;

import com.example.studycore.application.mapper.StudentOutputMapper;
import com.example.studycore.application.usecase.student.input.CreateStudentInput;
import com.example.studycore.application.usecase.student.output.GetStudentOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Activity;
import com.example.studycore.domain.model.Folder;
import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.port.ActivityGateway;
import com.example.studycore.domain.port.FolderGateway;
import com.example.studycore.domain.port.LevelFolderTemplateGateway;
import com.example.studycore.domain.port.LevelProfileGateway;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.domain.port.UserGateway;
import com.example.studycore.infrastructure.service.email.NotifyEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class CreateStudentUseCase {

    private static final StudentOutputMapper MAPPER = StudentOutputMapper.INSTANCE;

    private final StudentGateway studentGateway;
    private final FolderGateway folderGateway;
    private final ActivityGateway activityGateway;
    private final LevelFolderTemplateGateway levelFolderTemplateGateway;
    private final LevelProfileGateway levelProfileGateway;
    private final NotifyEmailService notifyEmailService;
    private final PasswordEncoder passwordEncoder;
    private final UserGateway userGateway;

    @Transactional
    public GetStudentOutput execute(final CreateStudentInput input) {
        studentGateway.findByEmail(input.email().trim().toLowerCase()).ifPresent(existing -> {
            throw new BusinessException("User with email " + input.email() + " already exists.");
        });

        final var temporaryPassword = RandomStringUtils.randomAlphanumeric(10);

        log.info("EMAIL E SENHA ALUNO: {}: {}", input.email(), temporaryPassword);

        final var passwordHash = passwordEncoder.encode(temporaryPassword);

        final var teacher = userGateway.findById(input.teacherId())
                .orElseThrow(() -> new NotFoundException("Authenticated teacher not found."));

        final Student student = Student.create(
                input.name(),
                input.email(),
                passwordHash,
                input.avatarUrl(),
                input.phone(),
                input.teacherId(),
                input.levelProfileId(),
                input.classDays(),
                input.classTime(),
                input.classDuration(),
                input.classRate(),
                input.meetPlatform(),
                input.meetLink()
        );

        final var saved = studentGateway.save(student);

        createFoldersAndActivitiesFromLevelProfile(saved.getId(), input.levelProfileId(), input.teacherId());

        notifyEmailService.sendWelcomeStudent(
                saved.getEmail(),
                saved.getName(),
                teacher.getName(),
                temporaryPassword
        );

        return MAPPER.toGetStudentOutput(saved);
    }

    private void createFoldersAndActivitiesFromLevelProfile(UUID studentId, UUID levelProfileId, UUID teacherId) {
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
}

