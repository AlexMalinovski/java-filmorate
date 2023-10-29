package ru.yandex.practicum.filmorate.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.Operation;
import ru.yandex.practicum.filmorate.utils.AppProperties;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class UpdateEventDtoTest {
    @Autowired
    private Validator validator;

    @Test
    public void ifInvalidEventType_validationFails() {
        Set<ConstraintViolation<CreatedEventDto>> violations;
        violations = validator.validate(CreatedEventDto.builder()
                .eventId(1L)
                .userId(1L)
                .entityId(1L)
                .eventType(null)
                .operation(Operation.ADD)
                .timestamp(1698585287841L)
                .build());
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifInvalidOperation_validationFails() {
        Set<ConstraintViolation<CreatedEventDto>> violations;
        violations = validator.validate(CreatedEventDto.builder()
                .eventId(1L)
                .userId(1L)
                .entityId(1L)
                .eventType(EventType.FRIEND)
                .operation(null)
                .timestamp(1698585287841L)
                .build());
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifInvalidTimestamp_validationFails() {
        Set<ConstraintViolation<CreatedEventDto>> violations;
        violations = validator.validate(CreatedEventDto.builder()
                .eventId(1L)
                .userId(1L)
                .entityId(1L)
                .eventType(EventType.FRIEND)
                .operation(Operation.UPDATE)
                .timestamp(1761749462000L)
                .build());
        assertFalse(violations.isEmpty());
    }
}