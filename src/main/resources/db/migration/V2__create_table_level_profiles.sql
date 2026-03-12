CREATE TABLE level_profiles
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    code        VARCHAR(40)  NOT NULL UNIQUE,
    icon        VARCHAR(10)  NOT NULL DEFAULT '⭐',
    description TEXT,
    is_system   BOOLEAN      NOT NULL DEFAULT FALSE,
    created_by  UUID REFERENCES users (id),
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);