package com.example.studycore.application.usecase.student;

import com.example.studycore.application.mapper.StudentOutputMapper;
import com.example.studycore.application.service.StudentLevelStructureService;
import com.example.studycore.application.usecase.student.input.UpdateStudentInput;
import com.example.studycore.application.usecase.student.output.GetStudentOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.port.StudentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class UpdateStudentUseCase {

    private static final StudentOutputMapper MAPPER = StudentOutputMapper.INSTANCE;

    private final StudentGateway studentGateway;
    private final StudentLevelStructureService levelStructureService;

    @Transactional
    public GetStudentOutput execute(UpdateStudentInput input) {
        final var existing = studentGateway.findById(input.id())
                .orElseThrow(() -> new NotFoundException("Student not found with id: " + input.id()));

        if (!input.teacherId().equals(existing.getTeacherId())) {
            throw new BusinessException("Student does not belong to authenticated teacher.");
        }

        // Detectar mudança de nível (NOVO)
        final boolean levelProfileChanged = input.levelProfileId() != null &&
                !input.levelProfileId().equals(existing.getLevelProfileId());

        if (levelProfileChanged) {
            log.info("Level profile changed for student {} from {} to {}",
                    input.id(), existing.getLevelProfileId(), input.levelProfileId());

            // 1. Limpar a estrutura antiga (Hard Delete)
            log.debug("Deleting old folder structure for student {}", input.id());
            levelStructureService.deleteAllFoldersAndActivities(input.id());

            // 2. Criar nova estrutura
            log.debug("Creating new folder structure for student {} with level profile {}",
                    input.id(), input.levelProfileId());
            levelStructureService.createFoldersAndActivitiesFromLevelProfile(
                    input.id(),
                    input.levelProfileId(),
                    input.teacherId()
            );

            log.info("Successfully regenerated folder structure for student {}", input.id());
        }

        final Student updated = Student.with(
                existing.getId(),
                input.name() != null ? input.name().trim() : existing.getName(),
                existing.getEmail(),
                existing.getPasswordHash(),
                existing.getRole(),
                existing.getStatus(),
                input.avatarUrl() != null ? input.avatarUrl() : existing.getAvatarUrl(),
                input.phone() != null ? input.phone() : existing.getPhone(),
                existing.getTeacherId(),
                input.levelProfileId() != null ? input.levelProfileId() : existing.getLevelProfileId(),
                input.classDays() != null ? input.classDays() : existing.getClassDays(),
                input.classTime() != null ? input.classTime() : existing.getClassTime(),
                input.classDuration() != null ? input.classDuration() : existing.getClassDuration(),
                input.classRate() != null ? input.classRate() : existing.getClassRate(),
                input.meetPlatform() != null ? input.meetPlatform() : existing.getMeetPlatform(),
                input.meetLink() != null ? input.meetLink() : existing.getMeetLink(),
                existing.getStartDate(),
                existing.getNotesPrivate(),
                existing.getCreatedAt()
        );

        return MAPPER.toGetStudentOutput(studentGateway.save(updated));
    }
}

