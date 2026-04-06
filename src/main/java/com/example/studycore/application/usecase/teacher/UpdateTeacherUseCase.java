package com.example.studycore.application.usecase.teacher;

import com.example.studycore.application.usecase.teacher.input.UpdateTeacherInput;
import com.example.studycore.domain.exception.NotFoundException;
import com.example.studycore.domain.port.TeacherGateway;
import com.example.studycore.domain.port.WorkHourGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateTeacherUseCase {

    private final TeacherGateway teacherGateway;
    private final WorkHourGateway workHourGateway;

    @Transactional
    public void execute(final UpdateTeacherInput input) {

        final var existingTeacher = teacherGateway.findById(input.id())
                .orElseThrow(() -> new NotFoundException("Teacher not found with id: " + input.id()));

        final var updatedTeacher = existingTeacher.update(
                input.name(),
                input.email(),
                input.phone(),
                input.pixKey(),
                input.pixKeyType(),
                input.preferenceTheme()
        );

        if (input.workHour() != null) {
            final var existingWork = workHourGateway.findByTeacherId(input.id())
                    .orElseThrow(() -> new NotFoundException("WorkHour not found for teacher: " + input.id()));

            final var updateWorkHour = existingWork.update(
                    input.workHour().startTimeMorning(),
                    input.workHour().endTimeMorning(),
                    input.workHour().startTimeAfternoon(),
                    input.workHour().endTimeAfternoon()
            );

            workHourGateway.save(updateWorkHour);
        }

        teacherGateway.save(updatedTeacher);
    }
}

