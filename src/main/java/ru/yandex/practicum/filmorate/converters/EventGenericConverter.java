package ru.yandex.practicum.filmorate.converters;

import ru.yandex.practicum.filmorate.dto.CreatedEventDto;
import ru.yandex.practicum.filmorate.models.Event;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;

public class EventGenericConverter extends AbstractGenericConverter {
    @Override
    protected Map<ConvertiblePair, Function<Object, Object>> setSupportConversions() {
        return Map.ofEntries(
                new AbstractMap.SimpleEntry<>(
                        new ConvertiblePair(Event.class, CreatedEventDto.class), this::convertEventToCreatedEventDto)
        );
    }

    private CreatedEventDto convertEventToCreatedEventDto(Object eventObj) {
        Event event = (Event) eventObj;
        return CreatedEventDto.builder()
                .eventId(event.getEventId())
                .userId(event.getUserId())
                .entityId(event.getEntityId())
                .eventType(event.getEventType())
                .operation(event.getOperation())
                .timestamp(event.getTimestamp())
                .build();
    }
}
