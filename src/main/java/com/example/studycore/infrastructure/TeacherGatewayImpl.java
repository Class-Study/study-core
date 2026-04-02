package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.User;
import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.TeacherGateway;
import com.example.studycore.infrastructure.mapper.TeacherInfraMapper;
import com.example.studycore.infrastructure.persistence.auth.UserRepository;
import com.example.studycore.infrastructure.persistence.teacher.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TeacherGatewayImpl implements TeacherGateway {

    private static final TeacherInfraMapper MAPPER = TeacherInfraMapper.INSTANCE;

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;

    @Override
    @Transactional
    public User save(User teacher) {
        final var userEntity = MAPPER.toUserEntity(teacher);
        if (userEntity.getRole() == null) {
            userEntity.setRole(UserRole.TEACHER.name());
        }
        if (userEntity.getStatus() == null) {
            userEntity.setStatus(UserStatus.ACTIVE.name());
        }
        final var savedUser = userRepository.save(userEntity);

        final var teacherEntity = MAPPER.toTeacherEntity(teacher);
        teacherEntity.setId(savedUser.getId());
        final var savedTeacher = teacherRepository.save(teacherEntity);

        return MAPPER.fromUserAndTeacherEntity(savedUser, savedTeacher);
    }

    @Override
    @Transactional
    public User save(User teacher, String passwordHash) {
        final var userEntity = MAPPER.toUserEntity(teacher);
        userEntity.setPasswordHash(passwordHash);
        userEntity.setRole(UserRole.TEACHER.name());
        userEntity.setStatus(UserStatus.ACTIVE.name());
        final var savedUser = userRepository.save(userEntity);

        final var teacherEntity = MAPPER.toTeacherEntity(teacher);
        teacherEntity.setId(savedUser.getId());
        final var savedTeacher = teacherRepository.save(teacherEntity);

        return MAPPER.fromUserAndTeacherEntity(savedUser, savedTeacher);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id)
                .filter(u -> UserRole.TEACHER.name().equals(u.getRole()))
                .map(user -> MAPPER.fromUserAndTeacherEntity(
                        user,
                        teacherRepository.findById(id).orElse(null)
                ));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCaseAndRole(email.trim().toLowerCase(), UserRole.TEACHER.name())
                .map(user -> MAPPER.fromUserAndTeacherEntity(
                        user,
                        teacherRepository.findById(user.getId()).orElse(null)
                ));
    }

    @Override
    public List<User> findAll() {
        final var sort = Sort.by(Sort.Direction.ASC, "name");
        return userRepository.findByRole(UserRole.TEACHER.name(), sort)
                .stream()
                .map(user -> MAPPER.fromUserAndTeacherEntity(
                        user,
                        teacherRepository.findById(user.getId()).orElse(null)
                ))
                .toList();
    }

    @Override
    public void block(UUID id) {
        userRepository.findById(id)
                .filter(u -> UserRole.TEACHER.name().equals(u.getRole()))
                .ifPresent(u -> {
                    u.setStatus(UserStatus.BLOCKED.name());
                    userRepository.save(u);
                });
    }
}
