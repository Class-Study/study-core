CREATE TABLE activities
(
    id                 UUID PRIMARY KEY,
    folder_id          UUID         NOT NULL REFERENCES folders (id),
    level_subfolder_id UUID         REFERENCES study.level_subfolders (id) ON DELETE SET NULL,
    title              VARCHAR(255) NOT NULL,
    type               VARCHAR(100) NOT NULL,
    converted_html     TEXT         NOT NULL DEFAULT '',
    snapshot           TEXT,
    created_by         UUID         NOT NULL REFERENCES users (id),
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_activity_type CHECK (type IN ('EXERCISE', 'WORKSPACE', 'MATERIAL'))
);

CREATE INDEX idx_activities_level_subfolder ON activities (level_subfolder_id);
