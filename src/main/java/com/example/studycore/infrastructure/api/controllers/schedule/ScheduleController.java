package com.example.studycore.infrastructure.api.controllers.schedule;

import com.example.studycore.application.usecase.schedule.CreateExtraClassUseCase;
import com.example.studycore.application.usecase.schedule.DeleteExtraClassUseCase;
import com.example.studycore.application.usecase.schedule.ListWeekScheduleUseCase;
import com.example.studycore.infrastructure.api.ScheduleApi;
import com.example.studycore.infrastructure.api.controllers.schedule.request.CreateExtraClassRequest;
import com.example.studycore.infrastructure.api.controllers.schedule.response.ScheduleWeekResponse;
import com.example.studycore.infrastructure.mapper.SchedulerInfraMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}


