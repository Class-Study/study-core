CREATE TABLE IF NOT EXISTS study.students
(
    id               UUID PRIMARY KEY REFERENCES study.users (id) ON DELETE CASCADE,
    teacher_id       UUID          NOT NULL REFERENCES study.users (id),
    level_profile_id UUID REFERENCES study.level_profiles (id),
    class_days       VARCHAR[]     NOT NULL DEFAULT '{}',
    class_time       TIME          NOT NULL,
    class_duration   INT           NOT NULL DEFAULT 60,
    class_rate       NUMERIC(8, 2) NOT NULL DEFAULT 0,
    meet_platform    VARCHAR(50),
    meet_link        TEXT,
    start_date       DATE          NOT NULL,
    notes_private    TEXT,
    created_at       TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

ALTER TABLE study.students
    ALTER COLUMN level_profile_id DROP NOT NULL;

CREATE INDEX IF NOT EXISTS idx_students_teacher_id ON study.students (teacher_id);

