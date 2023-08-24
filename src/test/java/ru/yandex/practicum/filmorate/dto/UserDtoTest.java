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
class UserDtoTest {
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
        Set<ConstraintViolation<UserDto>> violations;
        violations = validator.validate(new UserDto(null, "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new UserDto("", "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new UserDto(" ", "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

    }

    @Test
    public void ifEmailInvalidFormat_validationFails() {
        Set<ConstraintViolation<UserDto>> violations;
        violations = validator.validate(new UserDto("email", "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new UserDto("email@", "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new UserDto("email.ru@", "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new UserDto("em@il@dfg.com", "login", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifEmailValidFormat_validationSuccess() {
        Set<ConstraintViolation<UserDto>> violations;
        violations = validator.validate(new UserDto("email@mail.ru", "login", "name", getValidBirthdayDate()));
        assertTrue(violations.isEmpty());
    }

    @Test
    public void ifLoginNullOrBlank_validationFails() {
        Set<ConstraintViolation<UserDto>> violations;
        violations = validator.validate(new UserDto("email@mail.ru", null, "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new UserDto("email@mail.ru", "", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new UserDto("email@mail.ru", " ", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifBirthdayNullOrBlank_validationFails() {
        Set<ConstraintViolation<UserDto>> violations;
        violations = validator.validate(new UserDto("email@mail.ru", null, "name", null));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new UserDto("email@mail.ru", "", "name", ""));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new UserDto("email@mail.ru", " ", "name", " "));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifLoginContainSpace_validationFails() {
        Set<ConstraintViolation<UserDto>> violations;
        violations = validator.validate(new UserDto("email@mail.ru", "log in", "name", getValidBirthdayDate()));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifBirthdayAfterNow_validationFails() {
        Set<ConstraintViolation<UserDto>> violations;
        LocalDate date = LocalDate.now().plusDays(1);
        violations = validator.validate(new UserDto("email@mail.ru", "login", "name",
                date.format(appProperties.getDefaultDateFormatter())));
        assertFalse(violations.isEmpty());
    }

    @Test
    public void ifBirthdayNowOrBefore_validationSuccess() {
        Set<ConstraintViolation<UserDto>> violations;
        LocalDate date = LocalDate.now();
        violations = validator.validate(new UserDto("email@mail.ru", "login", "name",
                date.format(appProperties.getDefaultDateFormatter())));
        assertTrue(violations.isEmpty());

        date = date.minusDays(1);
        violations = validator.validate(new UserDto("email@mail.ru", "login", "name",
                date.format(appProperties.getDefaultDateFormatter())));
        assertTrue(violations.isEmpty());
    }
}