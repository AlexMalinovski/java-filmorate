package ru.yandex.practicum.filmorate.storages;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.Operation;
import ru.yandex.practicum.filmorate.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Repository
@Primary
public class DbFeedStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeedByUser(User user) {
        List<Long> userIds = new ArrayList<>(user.getFriends());
        userIds.add(user.getId());

        Map<String, Object> params = new HashMap<>();
        params.put("userIds", userIds);
        params.put("userId", user.getId());
        params.put("likeType", EventType.LIKE.name());
        params.put("reviewType", EventType.REVIEW.name());

        String sql = "SELECT * FROM feed WHERE (user_id IN (:userIds) AND (event_type = :likeType OR event_type =" +
                " :reviewType)) OR (user_id = :userId) ORDER BY timestamp DESC";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        return namedParameterJdbcTemplate.query(sql, params, this::makeEvent);
    }

    @Override
    public void addEvent(Long userId, Long entityId, EventType et, Operation op) {
        Event event = Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(et)
                .operation(op)
                .timestamp(Instant.now().toEpochMilli())
                .build();
        String sql = "insert into feed (user_id, entity_id, event_type, operation, timestamp) values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, event.getUserId(), event.getEntityId(), event.getEventType().name(),
                event.getOperation().name(),
                event.getTimestamp());
    }

    private Event makeEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id"))
                .entityId(rs.getLong("entity_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .timestamp(rs.getLong("timestamp"))
                .build();
    }
}
