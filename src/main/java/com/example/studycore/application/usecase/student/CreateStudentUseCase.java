package com.example.studycore.application.usecase.student;

import com.example.studycore.application.mapper.StudentOutputMapper;
import com.example.studycore.application.usecase.student.input.CreateStudentInput;
import com.example.studycore.application.usecase.student.output.GetStudentOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.model.User;
import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.domain.port.UserGateway;
import com.example.studycore.infrastructure.service.email.NotifyEmailService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateStudentUseCase {

    private static final StudentOutputMapper MAPPER = StudentOutputMapper.INSTANCE;

    private final StudentGateway studentGateway;
    private final NotifyEmailService notifyEmailService;
    private final PasswordEncoder passwordEncoder;
    private final UserGateway userGateway;

    @Transactional
    public GetStudentOutput execute(final CreateStudentInput input) {
        studentGateway.findByEmail(input.email().trim().toLowerCase()).ifPresent(existing -> {
            throw new BusinessException("User with email " + input.email() + " already exists.");
        });

        final var temporaryPassword = RandomStringUtils.randomAlphanumeric(10);
        final var passwordHash = passwordEncoder.encode(temporaryPassword);

        final var teacher = userGateway.findById(input.teacherId())
                .orElseThrow(() -> new NotFoundException("Authenticated teacher not found."));

        final Student student = Student.with(
                null,
                input.name().trim(),
                input.email().trim().toLowerCase(),
                passwordHash,
                UserRole.STUDENT,
                UserStatus.ACTIVE,
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
                null,
                null
        );

        final var saved = studentGateway.save(student);

        notifyEmailService.sendWelcomeStudent(
                saved.getEmail(),
                saved.getName(),
                teacher.getName(),
                temporaryPassword
        );

        return MAPPER.toGetStudentOutput(saved);
    }
}

