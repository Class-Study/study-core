package com.example.studycore.application.usecase.classroom;

import com.example.studycore.application.usecase.classroom.input.CreateExtraClassInput;
import com.example.studycore.domain.exception.ConflictException;
import com.example.studycore.domain.model.Classroom;
import com.example.studycore.domain.port.ClassroomGateway;
import com.example.studycore.domain.port.StudentGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class CreateExtraClassUseCase {

    private final ClassroomGateway classroomGateway;
    private final StudentGateway studentGateway;

    public void execute(CreateExtraClassInput input) {

        LocalTime endTime = input.startTime().plusMinutes(input.durationMin());

        if (studentGateway.existsRecurringClassOverlap(
                input.teacherId(),
                input.date().getDayOfWeek().name(),
                input.startTime(),
                endTime
        )) {
            throw new ConflictException("Já existe uma aula recorrente nesse dia e horário");
        }

        if (classroomGateway.existsByTeacherAndTimeOverlap(
                input.teacherId(),
                input.date(),
                input.startTime(),
                endTime
        )) {
            throw new ConflictException("Já existe uma aula nesse horário");
        }


        // create domain model
        final Classroom domain = Classroom.create(
                input.studentId(),
                input.teacherId(),
                input.date(),
                input.startTime(),
                input.durationMin(),
                input.title(),
                input.type()
        );

        classroomGateway.save(domain);
    }
}


