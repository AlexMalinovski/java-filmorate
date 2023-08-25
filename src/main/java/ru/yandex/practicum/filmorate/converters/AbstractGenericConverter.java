package ru.yandex.practicum.filmorate.converters;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public abstract class AbstractGenericConverter implements GenericConverter {
    private final Map<ConvertiblePair, Function<Object, Object>> conversions;

    public AbstractGenericConverter() {
        this.conversions = setSupportConversions();
    }
    protected abstract Map<ConvertiblePair, Function<Object, Object>> setSupportConversions();

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.copyOf(conversions.keySet());
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        var sourceObj = Optional.ofNullable(source)
                .orElseThrow(() -> new IllegalArgumentException("Конвертация null-объектов не поддерживается."));
        if (sourceType.getType() == targetType.getType()) {
            return source;
        }
        var converter = Optional
                .ofNullable(conversions.get(new ConvertiblePair(sourceType.getType(), targetType.getType())))
                .orElseThrow(() ->new IllegalArgumentException("Конвертация из " + sourceType.getType()
                        + " в " + targetType.getType() + " не поддерживается."));
        return converter.apply(sourceObj);
    }
}
