CREATE TABLE study.users
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(150) NOT NULL,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL DEFAULT 'STUDENT',
    avatar_url    TEXT,
    phone         VARCHAR(20),
    timezone      VARCHAR(60)  NOT NULL DEFAULT 'America/Sao_Paulo',
    status        VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    last_seen_at  TIMESTAMPTZ,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_user_role CHECK (role IN ('TEACHER', 'STUDENT')),
    CONSTRAINT chk_user_status CHECK (status IN ('ACTIVE', 'BLOCKED', 'INACTIVE'))
);