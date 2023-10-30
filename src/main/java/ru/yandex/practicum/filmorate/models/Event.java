package ru.yandex.practicum.filmorate.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Event {
    private final Long eventId;
    private final Long userId;
    private final Long entityId;
    private final EventType eventType;
    private final Operation operation;
    private final Long timestamp;
}
