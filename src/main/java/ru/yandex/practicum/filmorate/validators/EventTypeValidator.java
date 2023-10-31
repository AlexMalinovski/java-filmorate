package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.models.EventType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventTypeValidator implements ConstraintValidator<ValidEventType, EventType> {

    private List<String> acceptedValues;

    @Override
    public void initialize(ValidEventType annotation) {
        acceptedValues = Stream.of(EventType.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(EventType value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        return acceptedValues.contains(value.name());
    }
}
