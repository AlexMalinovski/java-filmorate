package ru.yandex.practicum.filmorate.validators;

import org.hibernate.validator.constraints.Range;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

@NotNull(message = "Id не может быть пустым")
@Range(min = 1L, message = "Id - целое число в пределах от 1 до Long.MAX_VALUE")
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(RequiredLongId.List.class)
@Constraint(validatedBy = {})
public @interface RequiredLongId {
    String message() default "Обязательное значение, целое число в пределах от 1 до Long.MAX_VALUE ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    public @interface List {
        RequiredLongId[] value();
    }
}
