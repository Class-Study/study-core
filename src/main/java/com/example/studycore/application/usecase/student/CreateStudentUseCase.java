package com.example.studycore.application.usecase.student;

import com.example.studycore.application.mapper.StudentOutputMapper;
import com.example.studycore.application.service.StudentLevelStructureService;
import com.example.studycore.application.usecase.student.input.CreateStudentInput;
import com.example.studycore.application.usecase.student.output.GetStudentOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.domain.port.UserGateway;
import com.example.studycore.infrastructure.service.email.NotifyEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import com.example.studycore.application.usecase.classroom.CreateClassroomUseCase;
import com.example.studycore.application.usecase.classroom.input.CreateClassroomInput;
import com.example.studycore.domain.model.enums.ScheduleType;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.time.LocalDate;
import java.util.List;
import org.apache.commons.lang3.RandomStringUtils;
import java.time.LocalTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class CreateStudentUseCase {

    private static final StudentOutputMapper MAPPER = StudentOutputMapper.INSTANCE;

    private final StudentGateway studentGateway;
    private final StudentLevelStructureService levelStructureService;
    private final NotifyEmailService notifyEmailService;
    private final PasswordEncoder passwordEncoder;
    private final UserGateway userGateway;
    private final CreateClassroomUseCase createClassroomUseCase;

    @Transactional
    public GetStudentOutput execute(final CreateStudentInput input) {
        studentGateway.findByEmailAndRole(input.email().trim().toLowerCase(), UserRole.STUDENT).ifPresent(existing -> {
            throw new BusinessException("User with email " + input.email() + " already exists.");
        });

        final var temporaryPassword = RandomStringUtils.randomAlphanumeric(10);

        log.info("EMAIL E SENHA ALUNO: {}: {}", input.email(), temporaryPassword);

        final var passwordHash = passwordEncoder.encode(temporaryPassword);

        final var teacher = userGateway.findById(input.teacherId())
                .orElseThrow(() -> new NotFoundException("Authenticated teacher not found."));

        final var contractEndDate = input.startDate().plusMonths(input.contractMonths());


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
                input.meetLink(),
                input.startDate(),
                input.contractMonths(),
                contractEndDate
        );

        final var saved = studentGateway.save(student);

        levelStructureService.createFoldersAndActivitiesFromLevelProfile(saved.getId(), input.levelProfileId(), input.teacherId());

        // create recurring classes for the student according to the provided days, time and contract period
        tryCreateRecurringClasses(saved.getId(), input.classDays(), input.startDate(), contractEndDate, input.classTime(), input.classDuration(), input.teacherId());

        notifyEmailService.sendWelcomeStudent(
                saved.getEmail(),
                saved.getName(),
                teacher.getName(),
                temporaryPassword
        );

        return MAPPER.toGetStudentOutput(saved, temporaryPassword);
    }

    private void tryCreateRecurringClasses(
            final UUID studentId,
            final List<String> classDays,
            final LocalDate startDate,
            final LocalDate contractEndDate,
            final LocalTime classTime,
            final Integer durationMin,
            final UUID teacherId
    ) {
        if (classDays == null || classDays.isEmpty() || classTime == null || durationMin == null) return;

        for (String day : classDays) {
            final DayOfWeek dow;
            try {
                dow = DayOfWeek.valueOf(day);
            } catch (Exception e) {
                log.warn("Invalid day provided for recurring classes: {}", day);
                continue;
            }

            LocalDate current = startDate.with(TemporalAdjusters.nextOrSame(dow));
            while (!current.isAfter(contractEndDate)) {
                try {
                    final var input = new CreateClassroomInput(
                            studentId,
                            teacherId,
                            ScheduleType.RECURRING,
                            current,
                            classTime,
                            durationMin,
                            "Aula recorrente"
                    );
                    createClassroomUseCase.execute(input);
                    log.debug("Created recurring class for student {} on {}", studentId, current);
                } catch (Exception ex) {
                    // If there is a conflict or other issue creating a single class, log and continue with others
                    log.warn("Could not create recurring class for student {} on {}: {}", studentId, current, ex.getMessage());
                }

                current = current.plusWeeks(1);
            }
        }
    }

}
