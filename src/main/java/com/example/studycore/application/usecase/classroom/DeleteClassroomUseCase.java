package com.example.studycore.application.usecase.classroom;

import com.example.studycore.domain.port.ClassroomGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeleteClassroomUseCase {

    private final ClassroomGateway classroomGateway;

    public void execute(UUID id) {
        if (id == null) throw new IllegalArgumentException("id cannot be null");
        classroomGateway.deleteById(id);
    }
}
