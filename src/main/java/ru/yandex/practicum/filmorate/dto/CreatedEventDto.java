package ru.yandex.practicum.filmorate.dto;


import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.Operation;
import ru.yandex.practicum.filmorate.validators.ValidEventType;
import ru.yandex.practicum.filmorate.validators.ValidOperation;
import ru.yandex.practicum.filmorate.validators.ValidTimestamp;

import javax.validation.constraints.Positive;

@Data
@Builder
@RequiredArgsConstructor
public class CreatedEventDto {

    @Positive(message = "Id события должен быть положительным числом")
    private final Long eventId;
    @Positive(message = "Id пользователя должен быть положительным числом")
    private final Long userId;
    @Positive(message = "Id сущности, над которой происходит событие, должен быть положительным числом")
    private final Long entityId;
    @ValidEventType
    private final EventType eventType;
    @ValidOperation
    private final Operation operation;
    @ValidTimestamp
    private final Long timestamp;
}
