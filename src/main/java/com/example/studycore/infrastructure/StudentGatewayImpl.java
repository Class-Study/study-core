package com.example.studycore.infrastructure;

import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.model.enums.UserRole;
import com.example.studycore.domain.model.enums.UserStatus;
import com.example.studycore.domain.port.StudentGateway;
import com.example.studycore.infrastructure.mapper.StudentInfraMapper;
import com.example.studycore.infrastructure.persistence.auth.UserEntity;
import com.example.studycore.infrastructure.persistence.auth.UserRepository;
import com.example.studycore.infrastructure.persistence.student.StudentEntity;
import com.example.studycore.infrastructure.persistence.student.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class StudentGatewayImpl implements StudentGateway {

    private static final StudentInfraMapper STUDENT_INFRA_MAPPER = StudentInfraMapper.INSTANCE;

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public StudentGatewayImpl(UserRepository userRepository, StudentRepository studentRepository) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public Student save(Student student) {
        final UserEntity userEntity = STUDENT_INFRA_MAPPER.toUserEntity(student);
        userEntity.setRole(UserRole.STUDENT.name());
        if (userEntity.getStatus() == null) {
            userEntity.setStatus(UserStatus.ACTIVE.name());
        }

        final UserEntity savedUser = userRepository.save(userEntity);

        final StudentEntity studentEntity = STUDENT_INFRA_MAPPER.toStudentEntity(student);
        studentEntity.setId(savedUser.getId());
        final StudentEntity savedStudent = studentRepository.save(studentEntity);

        return STUDENT_INFRA_MAPPER.fromUserAndStudentEntity(savedUser, savedStudent);
    }

    @Override
    public Optional<Student> findById(UUID id) {
        final Optional<UserEntity> userOpt = userRepository.findById(id)
                .filter(u -> UserRole.STUDENT.name().equals(u.getRole()));

        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        final Optional<StudentEntity> studentOpt = studentRepository.findById(id);
        return studentOpt.map(studentEntity -> STUDENT_INFRA_MAPPER.fromUserAndStudentEntity(userOpt.get(), studentEntity));

    }

    @Override
    public Optional<Student> findByEmailAndRole(String email, UserRole role) {
        final Optional<UserEntity> userOpt = userRepository.findByEmailIgnoreCaseAndRole(email, role.name());
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        final UserEntity user = userOpt.get();
        if (!UserRole.STUDENT.name().equals(user.getRole())) {
            return Optional.of(STUDENT_INFRA_MAPPER.fromUserAndStudentEntity(user, null));
        }

        final Optional<StudentEntity> studentOpt = studentRepository.findById(user.getId());
        return Optional.of(STUDENT_INFRA_MAPPER.fromUserAndStudentEntity(user, studentOpt.orElse(null)));
    }

    @Override
    public List<Student> findAllByTeacherId(UUID teacherId) {
        return studentRepository.findByTeacherId(teacherId).stream()
                .map(studentEntity -> userRepository.findById(studentEntity.getId())
                        .filter(user -> UserRole.STUDENT.name().equals(user.getRole()))
                        .map(user -> STUDENT_INFRA_MAPPER.fromUserAndStudentEntity(user, studentEntity))
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .sorted(Comparator.comparing(Student::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Override
    public List<Student> findByLevelProfileId(UUID levelProfileId) {
        return studentRepository.findByLevelProfileId(levelProfileId).stream()
                .map(studentEntity -> userRepository.findById(studentEntity.getId())
                        .filter(user -> UserRole.STUDENT.name().equals(user.getRole()))
                        .map(user -> STUDENT_INFRA_MAPPER.fromUserAndStudentEntity(user, studentEntity))
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    @Override
    public void block(UUID id) {
        userRepository.findById(id)
                .filter(user -> UserRole.STUDENT.name().equals(user.getRole()))
                .ifPresent(user -> {
                    user.setStatus(UserStatus.BLOCKED.name());
                    userRepository.save(user);
                });
    }

    @Override
    public void unblock(UUID id) {
        userRepository.findById(id)
                .filter(user -> UserRole.STUDENT.name().equals(user.getRole()))
                .ifPresent(user -> {
                    user.setStatus(UserStatus.ACTIVE.name());
                    userRepository.save(user);
                });
    }

    @Override
    public List<Student> searchByNameOrEmail(String q) {
        if (q == null || q.isBlank()) return java.util.List.of();

        final var sort = Sort.by(Sort.Direction.ASC, "name");
        final List<UserEntity> users = userRepository.searchByRoleAndNameOrEmail(UserRole.STUDENT.name(), q.trim(), sort);

        return users.stream()
                .filter(user -> UserRole.STUDENT.name().equals(user.getRole()))
                .map(user -> studentRepository.findById(user.getId())
                        .map(studentEntity -> STUDENT_INFRA_MAPPER.fromUserAndStudentEntity(user, studentEntity))
                        .orElse(STUDENT_INFRA_MAPPER.fromUserAndStudentEntity(user, null)))
                .toList();
    }

    @Override
    public boolean existsRecurringClassOverlap(UUID teacherId, String classDays, LocalTime startClass, LocalTime endClass) {
        return studentRepository.existsRecurringClassOverlap(teacherId, classDays, startClass, endClass);
    }

    @Override
    public List<Student> findRecurringConflicts(
            UUID teacherId,
            List<String> days,
            LocalTime classTime,
            int durationMin,
            LocalDate startDate,
            LocalDate contractEndDate
    ) {
        LocalTime classTimeEnd = classTime.plusMinutes(durationMin);

        List<StudentEntity> entities = studentRepository.findRecurringConflictsRaw(
                teacherId,
                days,
                classTime,
                classTimeEnd,
                startDate,
                contractEndDate
        );

        return entities.stream()
                .map(studentEntity -> userRepository.findById(studentEntity.getId())
                        .filter(user -> UserRole.STUDENT.name().equals(user.getRole()))
                        .map(user -> STUDENT_INFRA_MAPPER.fromUserAndStudentEntity(user, studentEntity))
                        .orElse(null))
                .filter(java.util.Objects::nonNull)
                .toList();
    }
}

