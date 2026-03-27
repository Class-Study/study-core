package com.example.studycore.application.usecase.schedule;

import com.example.studycore.application.usecase.schedule.input.CreateExtraClassInput;
import com.example.studycore.domain.model.ExtraClass;
import com.example.studycore.domain.port.ExtraClassGateway;
import com.example.studycore.domain.port.StudentGateway;
import org.springframework.stereotype.Service;

@Service
public class CreateExtraClassUseCase {

    private final ExtraClassGateway extraClassGateway;
    private final StudentGateway studentGateway;

    public CreateExtraClassUseCase(ExtraClassGateway extraClassGateway, StudentGateway studentGateway) {
        this.extraClassGateway = extraClassGateway;
        this.studentGateway = studentGateway;
    }

    public void execute(CreateExtraClassInput input) {
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


