package ru.yandex.practicum.filmorate.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotNull(message = "Timestamp не может быть null")
@Documented
@Constraint(validatedBy = TimestampValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTimestamp {
    String message() default "Timestamp не должен быть больше текущего времени";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
