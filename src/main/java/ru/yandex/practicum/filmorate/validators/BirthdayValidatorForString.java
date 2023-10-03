package ru.yandex.practicum.filmorate.validators;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.utils.AppProperties;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

@RequiredArgsConstructor
public class BirthdayValidatorForString implements ConstraintValidator<Birthday, String> {
    private final AppProperties appProperties;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (s == null || s.isBlank()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("День рождения не может быть null или пустой")
                    .addConstraintViolation();
            return false;
        }
        try {
            LocalDate now = LocalDate.now();
            LocalDate parsed = LocalDate.parse(s, appProperties.getDefaultDateFormatter());
            if (now.isAfter(parsed) || now.isEqual(parsed)) {
                return true;
            }
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("День рождения не может быть в будущем")
                    .addConstraintViolation();
            return false;
        } catch (Exception ex) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Формат даты не соответствует заданному: "
                            + appProperties.getDefaultDateFormat())
                    .addConstraintViolation();
            return false;
        }
    }
}
