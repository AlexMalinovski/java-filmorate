package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.models.Operation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OperationValidator implements ConstraintValidator<ValidOperation, CharSequence> {

    private List<String> acceptedValues;

    @Override
    public void initialize(ValidOperation annotation) {
        acceptedValues = Stream.of(Operation.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        return acceptedValues.contains(value.toString());
    }
}
