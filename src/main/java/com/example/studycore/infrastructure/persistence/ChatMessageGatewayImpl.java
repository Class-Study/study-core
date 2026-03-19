package com.example.studycore.infrastructure.persistence;

import com.example.studycore.domain.model.ChatMessage;
import com.example.studycore.domain.port.ChatMessageGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageGatewayImpl implements ChatMessageGateway {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<ChatMessage> findByActivityId(UUID activityId) {
        String sql = """
            SELECT 
                cm.id,
                cm.activity_id,
                cm.user_id,
                cm.content,
                cm.sent_at,
                cm.read_at,
                u.name as author_name
            FROM chat_messages cm
            JOIN users u ON cm.user_id = u.id
            WHERE cm.activity_id = :activity_id
            ORDER BY cm.sent_at ASC
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("activity_id", activityId);

        return namedParameterJdbcTemplate.query(sql, params, this::mapToChatMessage);
    }

    @Override
    public ChatMessage save(ChatMessage message) {
        String sql = """
            INSERT INTO chat_messages (id, activity_id, user_id, content, sent_at, read_at)
            VALUES (:id, :activity_id, :user_id, :content, :sent_at, :read_at)
            RETURNING id, activity_id, user_id, content, sent_at, read_at
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", message.getId())
                .addValue("activity_id", message.getActivityId())
                .addValue("user_id", message.getUserId())
                .addValue("content", message.getContent())
                .addValue("sent_at", message.getSentAt())
                .addValue("read_at", message.getReadAt());

        ChatMessage result = namedParameterJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
            ChatMessage saved = new ChatMessage();
            saved.setId((UUID) rs.getObject("id"));
            saved.setActivityId((UUID) rs.getObject("activity_id"));
            saved.setUserId((UUID) rs.getObject("user_id"));
            saved.setContent(rs.getString("content"));
            saved.setSentAt(rs.getObject("sent_at", java.time.OffsetDateTime.class));
            saved.setReadAt(rs.getObject("read_at", java.time.OffsetDateTime.class));
            return saved;
        });

        if (result != null) {
            log.info("[Chat] Mensagem salva | id={} | activityId={} | userId={}",
                    result.getId(), result.getActivityId(), result.getUserId());
        }

        return result;
    }

    @Override
    public void markAsRead(UUID activityId, UUID userId) {
        String sql = """
            UPDATE chat_messages
            SET read_at = NOW()
            WHERE activity_id = :activity_id
                AND user_id != :user_id
                AND read_at IS NULL
        """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("activity_id", activityId)
                .addValue("user_id", userId);

        int updated = namedParameterJdbcTemplate.update(sql, params);
        log.info("[Chat] Mensagens marcadas como lidas | activityId={} | userId={} | count={}",
                activityId, userId, updated);
    }

    private ChatMessage mapToChatMessage(ResultSet rs, int rowNum) throws SQLException {
        return new ChatMessage(
                (UUID) rs.getObject("id"),
                (UUID) rs.getObject("activity_id"),
                (UUID) rs.getObject("user_id"),
                rs.getString("content"),
                rs.getObject("sent_at", java.time.OffsetDateTime.class),
                rs.getObject("read_at", java.time.OffsetDateTime.class),
                rs.getString("author_name")
        );
    }
}


