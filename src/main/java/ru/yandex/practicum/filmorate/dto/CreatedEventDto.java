package ru.yandex.practicum.filmorate.dto;


import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.Operation;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import java.time.Instant;

@Data
@Builder
@RequiredArgsConstructor
public class CreatedEventDto {

    @Positive
    private final Long eventId;
    @Positive
    private final Long userId;
    @Positive
    private final Long entityId;
    @NotNull
    private final EventType eventType;
    @NotNull
    private final Operation operation;
    @PastOrPresent
    private final Instant timestamp;
}
