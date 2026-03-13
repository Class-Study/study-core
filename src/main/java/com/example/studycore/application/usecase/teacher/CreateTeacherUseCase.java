package com.example.studycore.application.usecase.teacher;

import com.example.studycore.application.mapper.TeacherOutputMapper;
import com.example.studycore.application.usecase.teacher.input.CreateTeacherInput;
import com.example.studycore.application.usecase.teacher.output.GetTeacherOutput;
import com.example.studycore.domain.exception.BusinessException;
import com.example.studycore.domain.model.User;
import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.TeacherGateway;
import com.example.studycore.infrastructure.service.email.NotifyEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class CreateTeacherUseCase {

    private static final TeacherOutputMapper MAPPER = TeacherOutputMapper.INSTANCE;
    private static final int TEMPORARY_PASSWORD_LENGTH = 10;

    private final TeacherGateway teacherGateway;
    private final PasswordEncoder passwordEncoder;
    private final NotifyEmailService notifyEmailService;

    @Transactional
    public GetTeacherOutput execute(final CreateTeacherInput input) {
        teacherGateway.findByEmail(input.email().trim().toLowerCase())
                .ifPresent(existingTeacher -> {
                    throw new BusinessException("Teacher with email " + input.email() + " already exists.");
                });

        final var temporaryPassword = RandomStringUtils.randomAlphanumeric(TEMPORARY_PASSWORD_LENGTH);

        log.info("EMAIL E SENHA PROFESSOR: {}: {}", input.email(), temporaryPassword);

        final var passwordHash = passwordEncoder.encode(temporaryPassword);

        final var teacher = User.create(
                input.name(),
                input.email(),
                passwordHash,
                UserRole.TEACHER,
                UserStatus.ACTIVE,
                input.avatarUrl(),
                input.phone()
        );

        final var savedTeacher = teacherGateway.save(teacher, passwordHash);

        notifyEmailService.sendWelcomeTeacher(
                savedTeacher.getEmail(),
                savedTeacher.getName(),
                temporaryPassword
        );

        return MAPPER.toGetTeacherOutput(savedTeacher);
    }
}


