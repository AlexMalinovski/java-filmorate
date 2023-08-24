package ru.yandex.practicum.filmorate.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.configs.AppProperties;

import java.time.LocalDate;
import java.time.Month;

@RequiredArgsConstructor
public class FilmReleaseValidatorForString implements ConstraintValidator<FilmRelease, String> {
    private final LocalDate minDate = LocalDate.of(1895, Month.DECEMBER, 28);
    private final AppProperties appProperties;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (s == null || s.isBlank()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Дата релиза не может быть null или пустой")
                    .addConstraintViolation();
            return false;
        }
        try {
            LocalDate parsed = LocalDate.parse(s, appProperties.getDefaultDateFormatter());
            if (minDate.isEqual(parsed) || minDate.isBefore(parsed)) {
                return true;
            }
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Дата релиза — не раньше: "
                            + minDate.format(appProperties.getDefaultDateFormatter()))
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
