CREATE TABLE study.level_folder_templates (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    level_folder_id  UUID         NOT NULL REFERENCES study.level_folders(id) ON DELETE CASCADE,
    title            VARCHAR(255) NOT NULL,
    type             VARCHAR(20)  NOT NULL DEFAULT 'EXERCISE',
    original_filename VARCHAR(255),
    converted_html   TEXT         NOT NULL,
    created_by       UUID         NOT NULL REFERENCES study.users(id),
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_template_type CHECK (type IN ('EXERCISE', 'WORKSPACE'))
);

CREATE INDEX idx_templates_level_folder_id
    ON study.level_folder_templates(level_folder_id);

