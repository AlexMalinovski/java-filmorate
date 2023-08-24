package ru.yandex.practicum.filmorate.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.lang.annotation.*;

@NotNull(message = "Логин не может быть пустым")
@Pattern(regexp = "^\\S+$", message = "Логин не может содержать пробелы")
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(RequiredLogin.List.class)
@Constraint(validatedBy = {})
public @interface RequiredLogin {
    String message() default "Обязательное значение без пробелов";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        RequiredLogin[] value();
    }
}
