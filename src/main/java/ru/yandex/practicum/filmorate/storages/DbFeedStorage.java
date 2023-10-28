package ru.yandex.practicum.filmorate.storages;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.Operation;
import ru.yandex.practicum.filmorate.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
@Primary
public class DbFeedStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeedByUser(User user) {
        List<Event> feed = new ArrayList<>();
        Set<Long> userFriends = user.getFriends();
        for (Long userFriendId : userFriends) {
            String sql = "select * from feed where user_id = ? and (event_type = ? or event_type = ?)";
            feed.addAll(jdbcTemplate.query(sql, this::mapRowToEvent, userFriendId, EventType.LIKE,
                    EventType.REVIEW));
        }
        return feed.stream()
                .sorted((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()))
                .collect(Collectors.toList());
    }

    @Override
    public void addEvent(Long userId, Long entityId, EventType et, Operation op) {
        Event event = Event.builder()
                .userId(userId)
                .entityId(entityId)
                .eventType(et)
                .operation(op)
                .timestamp(Instant.now())
                .build();
        String sql = "insert into feed (user_id, entity_id, event_type, operation, timestamp) values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, event.getUserId(), event.getEntityId(), event.getEventType(), event.getOperation(),
                event.getTimestamp().toEpochMilli());
    }

    private Event mapRowToEvent(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id"))
                .entityId(rs.getLong("entity_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .timestamp(rs.getTimestamp("timestamp").toInstant())
                .build();
    }
}
