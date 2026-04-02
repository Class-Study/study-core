package com.example.studycore.application.usecase.classroom;

import com.example.studycore.application.usecase.classroom.output.ClassroomUpdatedInput;
import com.example.studycore.domain.port.ClassroomGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateClassroomUseCase {

    private final ClassroomGateway classroomGateway;

    public void execute(final ClassroomUpdatedInput input) {

        final var classroom = classroomGateway.findById(input.id())
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + input.id()));

        classroom.updateDateAndTime(
                input.newDate(),
                input.newTime()
        );

        classroomGateway.save(classroom);
    }
}
