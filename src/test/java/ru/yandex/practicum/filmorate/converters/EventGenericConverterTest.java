package ru.yandex.practicum.filmorate.converters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import ru.yandex.practicum.filmorate.dto.CreatedEventDto;
import ru.yandex.practicum.filmorate.dto.CreatedGenreDto;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.Operation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class EventGenericConverterTest {

    @Autowired
    private ConversionService conversionService;

    @Test
    public void event_to_createdEventDto_isConvertible() {
        Event event = Event.builder()
                .eventId(1L)
                .userId(1L)
                .entityId(1L)
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .timestamp(1698585287841L)
                .build();
        CreatedEventDto dto = conversionService.convert(event, CreatedEventDto.class);
        assertNotNull(dto);
        assertEquals(event.getEventId(), dto.getEventId());
        assertEquals(event.getUserId(), dto.getUserId());
        assertEquals(event.getEntityId(), dto.getEntityId());
        assertEquals(event.getEventType(), dto.getEventType());
        assertEquals(event.getOperation(), dto.getOperation());
        assertEquals(event.getTimestamp(), dto.getTimestamp());
    }

}