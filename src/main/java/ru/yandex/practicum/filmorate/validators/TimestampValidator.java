package ru.yandex.practicum.filmorate.validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TimestampValidator implements ConstraintValidator<ValidTimestamp, Long> {

    @Override
    public void initialize(ValidTimestamp constraintAnnotation) {
    }

    @Override
    public boolean isValid(Long timestamp, ConstraintValidatorContext context) {
        if (timestamp == null) {
            return false;
        }
        return timestamp <= System.currentTimeMillis();
    }
}
