package ru.yandex.practicum.filmorate.models;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class Event {

    private final Long eventId;
    private final Long userId;
    private final Long entityId;
    private final EventType eventType;
    private final Operation operation;
    private final Instant timestamp;


}
