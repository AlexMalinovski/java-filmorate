package ru.yandex.practicum.filmorate.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.filmorate.dto.CreatedUserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class UserToCreatedUserDto implements Converter<User, CreatedUserDto> {
    private final DateTimeFormatter dateFormatter;

    @Override
    public CreatedUserDto convert(User source) {
        return new CreatedUserDto(source.getId(),
                source.getEmail(),
                source.getLogin(),
                source.getName(),
                source.getBirthday().format(dateFormatter));
    }
}
