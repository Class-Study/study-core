package com.example.studycore.application.usecase.student;

import com.example.studycore.application.mapper.StudentOutputMapper;
import com.example.studycore.application.service.StudentLevelStructureService;
import com.example.studycore.application.usecase.student.input.CreateStudentInput;
import com.example.studycore.application.usecase.student.output.GetStudentOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.domain.port.UserGateway;
import com.example.studycore.infrastructure.service.email.NotifyEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
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

        levelStructureService.createFoldersAndActivitiesFromLevelProfile(saved.getId(), input.levelProfileId(), input.teacherId());

        notifyEmailService.sendWelcomeStudent(
                saved.getEmail(),
                saved.getName(),
                teacher.getName(),
                temporaryPassword
        );

        return MAPPER.toGetStudentOutput(saved);
    }

}


