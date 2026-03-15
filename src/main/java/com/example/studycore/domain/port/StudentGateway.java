package com.example.studycore.domain.port;

import com.example.studycore.domain.model.Student;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StudentGateway {
    Student save(Student student);
    Optional<Student> findById(UUID id);
    Optional<Student> findByEmail(String email);
    List<Student> findAllByTeacherId(UUID teacherId);
    List<Student> findByLevelProfileId(UUID levelProfileId);
    void block(UUID id);
    void unblock(UUID id);
}

