package com.example.studycore.infrastructure.persistence.student;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "students")
public class StudentEntity {

    @Id
    private UUID id;

    @Column(name = "teacher_id", nullable = false)
    private UUID teacherId;

    @Column(name = "level_profile_id")
    private UUID levelProfileId;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "class_days", nullable = false, columnDefinition = "varchar[]")
    private String[] classDays;

    @Column(name = "class_time", nullable = false)
    private LocalTime classTime;

    @Column(name = "class_duration", nullable = false)
    private Integer classDuration;

    @Column(name = "class_rate", nullable = false, precision = 8, scale = 2)
    private BigDecimal classRate;

    @Column(name = "meet_platform", length = 50)
    private String meetPlatform;

    @Column(name = "meet_link")
    private String meetLink;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "notes_private")
    private String notesPrivate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = OffsetDateTime.now();
        }
        if (classDays == null) {
            classDays = new String[]{};
        }
        if (classDuration == null) {
            classDuration = 60;
        }
        if (classRate == null) {
            classRate = BigDecimal.ZERO;
        }
    }
}
