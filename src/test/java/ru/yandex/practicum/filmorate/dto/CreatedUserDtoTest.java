package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.configs.AppProperties;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CreatedUserDtoTest {
    @Autowired
    private Validator validator;
    @Autowired
    private AppProperties appProperties;

    private String getValidBirthdayDate() {
        LocalDate date = LocalDate.of(1990, Month.JANUARY, 1);
        return date.format(appProperties.getDefaultDateFormatter());
    }

    @Test
    public void ifEmailNullOrBlank_validationFails() {
        Set<ConstraintViolation<CreatedUserDto>> violations;
        violations = validator.validate(new CreatedUserDto(1L, null, "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new CreatedUserDto(1L, "", "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new CreatedUserDto(1L, " ", "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

    }

    @Test
    public void ifEmailInvalidFormat_validationFails() {
        Set<ConstraintViolation<CreatedUserDto>> violations;
        violations = validator.validate(new CreatedUserDto(1L, "email", "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new CreatedUserDto(1L, "email@", "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new CreatedUserDto(1L, "email.ru@", "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new CreatedUserDto(1L, "em@il@dfg.com", "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifEmailValidFormat_validationSuccess() {
        Set<ConstraintViolation<CreatedUserDto>> violations;
        violations = validator.validate(new CreatedUserDto(1L, "email@mail.ru", "login", "name", getValidBirthdayDate()));
        assertTrue(violations.isEmpty());
    }

    @Test
    public void ifLoginNullOrBlank_validationFails() {
        Set<ConstraintViolation<CreatedUserDto>> violations;
        violations = validator.validate(new CreatedUserDto(1L, "email@mail.ru", null, "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new CreatedUserDto(1L, "email@mail.ru", "", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new CreatedUserDto(1L, "email@mail.ru", " ", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifBirthdayNullOrBlank_validationFails() {
        Set<ConstraintViolation<CreatedUserDto>> violations;
        violations = validator.validate(new CreatedUserDto(1L, "email@mail.ru", null, "name", null));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new CreatedUserDto(1L, "email@mail.ru", "", "name", ""));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new CreatedUserDto(1L, "email@mail.ru", " ", "name", " "));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifLoginContainSpace_validationFails() {
        Set<ConstraintViolation<CreatedUserDto>> violations;
        violations = validator.validate(new CreatedUserDto(1L, "email@mail.ru", "log in", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifBirthdayAfterNow_validationFails() {
        Set<ConstraintViolation<CreatedUserDto>> violations;
        LocalDate date = LocalDate.now().plusDays(1);
        violations = validator.validate(new CreatedUserDto(1L, "email@mail.ru", "login", "name",
                date.format(appProperties.getDefaultDateFormatter())));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifBirthdayNowOrBefore_validationSuccess() {
        Set<ConstraintViolation<CreatedUserDto>> violations;
        LocalDate date = LocalDate.now();
        violations = validator.validate(new CreatedUserDto(1L, "email@mail.ru", "login", "name",
                date.format(appProperties.getDefaultDateFormatter())));
        assertTrue(violations.isEmpty());

        date = date.minusDays(1);
        violations = validator.validate(new CreatedUserDto(1L, "email@mail.ru", "login", "name",
                date.format(appProperties.getDefaultDateFormatter())));
        assertTrue(violations.isEmpty());
    }

    @Test
    public void ifIdEqualsOrLessZero_validationFails() {
        Set<ConstraintViolation<CreatedUserDto>> violations;
        violations = validator.validate(new CreatedUserDto(-1L, "email@mail.ru",
                "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new CreatedUserDto(0L, "email@mail.ru",
                "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifIdMoreZero_validationSuccess() {
        Set<ConstraintViolation<CreatedUserDto>> violations;
        violations = validator.validate(new CreatedUserDto(1L, "email@mail.ru",
                "login", "name", getValidBirthdayDate()));
        assertTrue(violations.isEmpty());
    }


}