package ru.yandex.practicum.filmorate.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.utils.AppProperties;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UpdateFilmDtoTest {
    @Autowired
    private Validator validator;

    @Autowired
    private AppProperties appProperties;

    private String getValidReleaseDate() {
        LocalDate date = LocalDate.of(1990, Month.JANUARY, 1);
        return date.format(appProperties.getDefaultDateFormatter());
    }

    @Test
    public void ifNameIsNullOrBlank_validationFails() {
        Set<ConstraintViolation<UpdateFilmDto>> violations;
        violations = validator.validate(new UpdateFilmDto(1L, null, "descr", getValidReleaseDate(),
                120, new LongIdDto(1), new ArrayList<>()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new UpdateFilmDto(1L, "", "descr", getValidReleaseDate(),
                120, new LongIdDto(1), new ArrayList<>()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new UpdateFilmDto(1L, " ", "descr", getValidReleaseDate(),
                120, new LongIdDto(1), new ArrayList<>()));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifDescriptionIsNullOrBlank_validationSuccess() {
        Set<ConstraintViolation<UpdateFilmDto>> violations;
        violations = validator.validate(new UpdateFilmDto(1L, "name", null, getValidReleaseDate(),
                120, new LongIdDto(1), new ArrayList<>()));
        assertTrue(violations.isEmpty());

        violations = validator.validate(new UpdateFilmDto(1L, "name", "", getValidReleaseDate(),
                120, new LongIdDto(1), new ArrayList<>()));
        assertTrue(violations.isEmpty());
    }

    @Test
    public void ifDescriptionLengthEqual200_validationSuccess() {
        Set<ConstraintViolation<UpdateFilmDto>> violations;
        violations = validator.validate(new UpdateFilmDto(1L, "name",
                "а".repeat(200),getValidReleaseDate(), 120, new LongIdDto(1), new ArrayList<>()));
        assertTrue(violations.isEmpty());
    }

    @Test
    public void ifDescriptionLengthMore200_validationFails() {
        Set<ConstraintViolation<UpdateFilmDto>> violations;
        violations = validator.validate(new UpdateFilmDto(1L, "name",
                "а".repeat(201),getValidReleaseDate(), 120, new LongIdDto(1), new ArrayList<>()));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifReleaseDateIsNullOrBlank_validationFails() {
        Set<ConstraintViolation<UpdateFilmDto>> violations;
        violations = validator.validate(new UpdateFilmDto(1L, "name", "descr", null,
                120, new LongIdDto(1), new ArrayList<>()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new UpdateFilmDto(1L, "name", "descr", "",
                120, new LongIdDto(1), new ArrayList<>()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new UpdateFilmDto(1L, "name", "descr", " ",
                120, new LongIdDto(1), new ArrayList<>()));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifReleaseDateBefore28DEC1895_validationFails() {
        Set<ConstraintViolation<UpdateFilmDto>> violations;
        LocalDate date = LocalDate.of(1895, Month.DECEMBER, 27);
        violations = validator.validate(new UpdateFilmDto(1L, "name", "descr",
                date.format(appProperties.getDefaultDateFormatter()), 120, new LongIdDto(1), new ArrayList<>()));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifReleaseDateEqualsOrAfter28DEC1895_validationSuccess() {
        Set<ConstraintViolation<UpdateFilmDto>> violations;
        LocalDate date = LocalDate.of(1895, Month.DECEMBER, 28);
        violations = validator.validate(new UpdateFilmDto(1L, "name", "descr",
                date.format(appProperties.getDefaultDateFormatter()), 120, new LongIdDto(1), new ArrayList<>()));
        assertTrue(violations.isEmpty());

        date = LocalDate.of(2222, Month.DECEMBER, 28);
        violations = validator.validate(new UpdateFilmDto(1L, "name", "descr",
                date.format(appProperties.getDefaultDateFormatter()), 120, new LongIdDto(1), new ArrayList<>()));
        assertTrue(violations.isEmpty());
    }

    @Test
    public void ifDurationEqualsOrLessZero_validationFails() {
        Set<ConstraintViolation<UpdateFilmDto>> violations;
        violations = validator.validate(new UpdateFilmDto(1L, "name", "descr", getValidReleaseDate(),
                -1, new LongIdDto(1), new ArrayList<>()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new UpdateFilmDto(1L, "name", "descr", getValidReleaseDate(),
                0, new LongIdDto(1), new ArrayList<>()));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifDurationMoreZero_validationSuccess() {
        Set<ConstraintViolation<UpdateFilmDto>> violations;
        violations = validator.validate(new UpdateFilmDto(1L, "name", "descr", getValidReleaseDate(),
                1, new LongIdDto(1), new ArrayList<>()));
        assertTrue(violations.isEmpty());
    }

    @Test
    public void ifIdEqualsOrLessZero_validationFails() {
        Set<ConstraintViolation<UpdateFilmDto>> violations;
        violations = validator.validate(new UpdateFilmDto(-1L, "name", "descr",
                getValidReleaseDate(), 120, new LongIdDto(1), new ArrayList<>()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new UpdateFilmDto(0L, "name", "descr",
                getValidReleaseDate(), 120, new LongIdDto(1), new ArrayList<>()));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifIdMoreZero_validationSuccess() {
        Set<ConstraintViolation<UpdateFilmDto>> violations;
        violations = validator.validate(new UpdateFilmDto(1L, "name", "descr",
                getValidReleaseDate(), 120, new LongIdDto(1), new ArrayList<>()));
        assertTrue(violations.isEmpty());
    }
}