package com.example.studycore.application.usecase.schedule;

import com.example.studycore.application.usecase.schedule.input.CreateExtraClassInput;
import com.example.studycore.domain.exception.ConflictException;
import com.example.studycore.domain.model.ExtraClass;
import com.example.studycore.domain.model.Student;
import com.example.studycore.domain.port.ExtraClassGateway;
import com.example.studycore.domain.port.StudentGateway;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class CreateExtraClassUseCase {

    private final ExtraClassGateway extraClassGateway;
    private final StudentGateway studentGateway;

    public CreateExtraClassUseCase(ExtraClassGateway extraClassGateway, StudentGateway studentGateway) {
        this.extraClassGateway = extraClassGateway;
        this.studentGateway = studentGateway;
    }

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

        if (extraClassGateway.existsByTeacherAndTimeOverlap(
                input.teacherId(),
                input.date(),
                input.startTime(),
                endTime
        )) {
            throw new ConflictException("Já existe uma aula nesse horário");
        }


        // create domain model
        final ExtraClass domain = ExtraClass.create(
                input.studentId(),
                input.teacherId(),
                input.date(),
                input.startTime(),
                input.durationMin(),
                input.title(),
                input.type()
        );

        extraClassGateway.save(domain);
    }
}


