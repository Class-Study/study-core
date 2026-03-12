package com.example.studycore.domain.port;

import com.example.studycore.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TeacherGateway {

    User save(User teacher);

    User save(User teacher, String passwordHash);

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    void block(UUID id);
}


