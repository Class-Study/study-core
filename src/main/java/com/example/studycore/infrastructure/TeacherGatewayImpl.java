package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.User;
import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.TeacherGateway;
import com.example.studycore.infrastructure.mapper.TeacherInfraMapper;
import com.example.studycore.infrastructure.persistence.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TeacherGatewayImpl implements TeacherGateway {

    private static final TeacherInfraMapper TEACHER_INFRA_MAPPER = TeacherInfraMapper.INSTANCE;

    private final UserRepository userRepository;

    @Override
    public User save(User teacher) {
        final var entity = TEACHER_INFRA_MAPPER.toEntity(teacher);

        // Set default values if creating new teacher
        if (teacher.getId() == null) {
            entity.setRole(UserRole.TEACHER.name());
            entity.setStatus(UserStatus.ACTIVE.name());
        }

        final var savedEntity = userRepository.save(entity);
        return TEACHER_INFRA_MAPPER.fromEntity(savedEntity);
    }

    @Override
    public User save(User teacher, String passwordHash) {
        final var entity = TEACHER_INFRA_MAPPER.toEntity(teacher);

        // Set password hash for new teacher
        entity.setPasswordHash(passwordHash);
        entity.setRole(UserRole.TEACHER.name());
        entity.setStatus(UserStatus.ACTIVE.name());

        final var savedEntity = userRepository.save(entity);
        return TEACHER_INFRA_MAPPER.fromEntity(savedEntity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id)
                .filter(entity -> UserRole.TEACHER.name().equals(entity.getRole()))
                .map(TEACHER_INFRA_MAPPER::fromEntity);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCaseAndRole(email.trim().toLowerCase(), UserRole.TEACHER.name())
                .map(TEACHER_INFRA_MAPPER::fromEntity);
    }

    @Override
    public List<User> findAll() {
        final var sort = Sort.by(Sort.Direction.ASC, "name");
        return userRepository.findByRole(UserRole.TEACHER.name(), sort)
                .stream()
                .map(TEACHER_INFRA_MAPPER::fromEntity)
                .toList();
    }

    @Override
    public void block(UUID id) {
        userRepository.findById(id)
                .filter(entity -> UserRole.TEACHER.name().equals(entity.getRole()))
                .ifPresent(entity -> {
                    entity.setStatus(UserStatus.BLOCKED.name());
                    userRepository.save(entity);
                });
    }
}


