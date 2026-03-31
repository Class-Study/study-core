package com.example.studycore.infrastructure.api.controllers.schedule;

import com.example.studycore.application.usecase.classroom.CreateExtraClassUseCase;
import com.example.studycore.application.usecase.classroom.DeleteExtraClassUseCase;
import com.example.studycore.application.usecase.classroom.ListRescheduleOptionsUseCase;
import com.example.studycore.application.usecase.classroom.ListWeekScheduleUseCase;
import com.example.studycore.infrastructure.api.ScheduleApi;
import com.example.studycore.infrastructure.api.controllers.schedule.request.CreateExtraClassRequest;
import com.example.studycore.infrastructure.api.controllers.schedule.response.ScheduleWeekResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.RescheduleOptionResponse;
import com.example.studycore.infrastructure.mapper.SchedulerInfraMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ScheduleController implements ScheduleApi {

    private final ListWeekScheduleUseCase listWeekScheduleUseCase;
    private final CreateExtraClassUseCase createExtraClassUseCase;
    private final DeleteExtraClassUseCase deleteExtraClassUseCase;
    private final ListRescheduleOptionsUseCase listRescheduleOptionsUseCase;

    private final static SchedulerInfraMapper SCHEDULER_INFRA_MAPPER = SchedulerInfraMapper.INSTANCE;

    public ResponseEntity<ScheduleWeekResponse> week(
            @RequestParam LocalDate weekStart,
            @RequestParam LocalDate weekEnd,
            @RequestParam UUID teacherId
    ) {
        final var input = SCHEDULER_INFRA_MAPPER.toScheduleWeekInput(weekStart, weekEnd, teacherId);

        final var out = listWeekScheduleUseCase.execute(input);

        return ResponseEntity.ok(SCHEDULER_INFRA_MAPPER.toResponse(out));
    }

    public ResponseEntity<Void> createExtra(@Valid @RequestBody CreateExtraClassRequest req) {
        final var input = SCHEDULER_INFRA_MAPPER.toCreateExtraClassInput(req);

        createExtraClassUseCase.execute(input);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public ResponseEntity<Void> deleteExtra(@PathVariable UUID id) {
        deleteExtraClassUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RescheduleOptionResponse> getRescheduleOptions(UUID schedulerId, String scheduleType, LocalDate date) {
        final var teacherId = getAuthenticatedUserId();

        final var input = SCHEDULER_INFRA_MAPPER.toRescheduleOptionInput(teacherId, schedulerId, scheduleType, date);

        final var output = listRescheduleOptionsUseCase.execute(input);

        return ResponseEntity.ok(SCHEDULER_INFRA_MAPPER.toRescheduleOptionResponse(output));
    }

    private UUID getAuthenticatedUserId() {
        final var authentication = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString((String) authentication.getPrincipal());
    }
}


