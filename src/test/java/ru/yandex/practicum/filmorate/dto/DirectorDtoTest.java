package ru.yandex.practicum.filmorate.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.utils.AppProperties;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class DirectorDtoTest {

    @Autowired
    private Validator validator;

    @Autowired
    private AppProperties appProperties;

    @Test
    public void ifNameIsNullOrBlank_validationFails() {
        Set<ConstraintViolation<DirectorDto>> violations;

        violations = validator.validate(new DirectorDto(1, ""));
        assertFalse(violations.isEmpty());

        violations = validator.validate(new DirectorDto(2, " "));
        assertFalse(violations.isEmpty());
    }
}