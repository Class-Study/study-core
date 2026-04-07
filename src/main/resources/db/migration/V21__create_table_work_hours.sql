CREATE TABLE work_hours (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    teacher_id    UUID NOT NULL UNIQUE REFERENCES users(id),
    start_time_morning    TIME NOT NULL DEFAULT '08:00:00',
    end_time_morning      TIME NOT NULL DEFAULT '12:00:00',
    start_time_afternoon  TIME NOT NULL DEFAULT '13:00:00',
    end_time_afternoon    TIME NOT NULL DEFAULT '18:00:00',
    created_at    TIMESTAMP NOT NULL DEFAULT NOW()
);

