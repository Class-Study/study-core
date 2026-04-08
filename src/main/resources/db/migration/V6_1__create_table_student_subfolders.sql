CREATE TABLE study.student_subfolders
(
    id                 UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    folder_id          UUID         NOT NULL REFERENCES folders (id) ON DELETE CASCADE,
    level_subfolder_id UUID         REFERENCES study.level_subfolders (id) ON DELETE SET NULL,
    name               VARCHAR(255) NOT NULL,
    position           INTEGER      NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_student_subfolders_folder         ON study.student_subfolders (folder_id);
CREATE INDEX idx_student_subfolders_level_subfolder ON study.student_subfolders (level_subfolder_id);

