package com.example.studycore.infrastructure.api;

import com.example.studycore.infrastructure.api.controllers.schedule.request.ClassroomUpdatedRequest;
import com.example.studycore.infrastructure.api.controllers.schedule.request.CreateClassroomRequest;
import com.example.studycore.infrastructure.api.controllers.schedule.response.ScheduleWeekResponse;
import com.example.studycore.infrastructure.api.controllers.student.response.RescheduleOptionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    ResponseEntity<Void> createExtra(@Valid @RequestBody CreateClassroomRequest req);

    @DeleteMapping("/extra-class/{id}")
    ResponseEntity<Void> deleteExtra(@PathVariable UUID id);

    @GetMapping("/{schedulerId}/reschedule-options")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<RescheduleOptionResponse> getRescheduleOptions(
            @PathVariable UUID schedulerId,
            @RequestParam String scheduleType,
            @RequestParam LocalDate date
    );

    @PatchMapping("{scheduleId}/reschedule")
    @PreAuthorize("hasRole('TEACHER')")
    ResponseEntity<Void> reschedule(
            @PathVariable UUID scheduleId,
            @RequestBody ClassroomUpdatedRequest request
    );
}
