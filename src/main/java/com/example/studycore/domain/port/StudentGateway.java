package com.example.studycore.domain.port;

import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.model.enums.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentGateway {
    Student save(Student student);
    Optional<Student> findById(UUID id);
    Optional<Student> findByEmailAndRole(String email, UserRole role);
    List<Student> findAllByTeacherId(UUID teacherId);
    List<Student> findByLevelProfileId(UUID levelProfileId);
    void block(UUID id);
    void unblock(UUID id);
}

