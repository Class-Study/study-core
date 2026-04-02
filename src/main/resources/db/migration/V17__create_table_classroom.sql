-- Migration: create table extra_classes
-- Fields: id (uuid), student_id (uuid FK -> students.id), date, start_time, duration_min, title, timezone, start_at_utc, created_at, updated_at

CREATE TABLE IF NOT EXISTS classroom
(
    id           uuid PRIMARY KEY      DEFAULT gen_random_uuid(),
    student_id   uuid         NOT NULL REFERENCES students (id) ON DELETE CASCADE,
    teacher_id   uuid         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    type         varchar(50)  NOT NULL,
    date         date         NOT NULL,
    start_time   time         NOT NULL,
    duration_min integer      NOT NULL,
    title        varchar(255) NOT NULL,
    created_at   timestamptz  NOT NULL DEFAULT now(),
    updated_at   timestamptz  NOT NULL DEFAULT now()
);
CREATE INDEX IF NOT EXISTS idx_extra_student_id ON classroom (student_id);

