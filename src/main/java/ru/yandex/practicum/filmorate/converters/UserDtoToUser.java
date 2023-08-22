package ru.yandex.practicum.filmorate.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class UserDtoToUser implements Converter<UserDto, User> {
    private final DateTimeFormatter dateFormatter;

    @Override
    public User convert(UserDto source) {
        String name = source.isEmptyName() ? source.getLogin() : source.getName();
        LocalDate birthDate = LocalDate.parse(source.getBirthday(), dateFormatter);
        return new User(null, source.getEmail(), source.getLogin(), name, birthDate);
    }
}
