package ru.yandex.practicum.filmorate.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NotNull(message = "Тип события не может быть null")
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR,
        ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = OperationValidator.class)
public @interface ValidOperation {
    String message() default "Значение события может быть только REMOVE, ADD или UPDATE";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
