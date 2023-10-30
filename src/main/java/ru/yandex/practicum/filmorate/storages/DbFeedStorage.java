package ru.yandex.practicum.filmorate.storages;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.Operation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RequiredArgsConstructor
@Repository
@Primary
public class DbFeedStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getUserFeed(Map<String, Object> params) {
        String sql = "SELECT * FROM feed WHERE (user_id IN (:userIds) AND (event_type = :likeType OR event_type =" +
                " :reviewType)) OR (user_id = :userId) ORDER BY timestamp";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        return namedParameterJdbcTemplate.query(sql, params, this::makeEvent);
    }

    @Override
    public Event addEvent(Event event) {
        String sql = "insert into feed (user_id, entity_id, event_type, operation, timestamp) values (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"event_id"});
            stmt.setLong(1, event.getUserId());
            stmt.setLong(2, event.getEntityId());
            stmt.setString(3, event.getEventType().name());
            stmt.setString(4, event.getOperation().name());
            stmt.setLong(5, event.getTimestamp());
            return stmt;
        }, keyHolder);
        return findEventById((Objects.requireNonNull(keyHolder.getKey()).longValue()));
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

    private Event findEventById(long id) {
        String sql = "select * from feed where event_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::makeEvent, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("События с id %s нет в системе", id));
        }
    }
}
