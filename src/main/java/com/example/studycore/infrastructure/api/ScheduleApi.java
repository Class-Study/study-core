package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.schedule.request.CreateExtraClassRequest;
import com.example.studycore.infrastructure.api.controllers.schedule.response.ScheduleEventResponse;
import com.example.studycore.infrastructure.api.controllers.schedule.response.ScheduleWeekResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RequestMapping("/schedule")
public interface ScheduleApi {

    @GetMapping("/week")
    ResponseEntity<ScheduleWeekResponse> week(
            @RequestParam LocalDate weekStart,
            @RequestParam LocalDate weekEnd,
            @RequestParam UUID teacherId
    );

    @PostMapping("/extra-class")
    ResponseEntity<Void> createExtra(@Valid @RequestBody CreateExtraClassRequest req);

    @DeleteMapping("/extra-class/{id}")
    ResponseEntity<Void> deleteExtra(@PathVariable UUID id);
}
