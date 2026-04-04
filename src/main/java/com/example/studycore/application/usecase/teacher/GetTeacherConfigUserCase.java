package com.example.studycore.application.usecase.teacher;

import com.example.studycore.application.usecase.teacher.output.GetTeacherConfigOutput;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.TeacherGateway;
import com.example.studycore.domain.port.WorkHourGateway;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class GetTeacherConfigUserCase {

    private final TeacherGateway teacherGateway;
    private final WorkHourGateway workHourGateway;

    public GetTeacherConfigOutput execute(UUID teacherId) {
        final var teacher = teacherGateway.findById(teacherId)
                .orElseThrow(() -> new NotFoundException("Teacher not found: " + teacherId));

        final var workHour = workHourGateway.findByTeacherId(teacherId)
                .orElseThrow(() -> new NotFoundException("WorkHour not found for teacher: " + teacherId));

        return new GetTeacherConfigOutput(
                teacher.getPixKey(),
                teacher.getPixKeyType(),
                workHour.getStartTimeMorning(),
                workHour.getEndTimeMorning(),
                workHour.getStartTimeAfternoon(),
                workHour.getEndTimeAfternoon()
        );
    }
}
