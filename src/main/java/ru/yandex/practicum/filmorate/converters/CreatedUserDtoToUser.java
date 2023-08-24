package ru.yandex.practicum.filmorate.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.filmorate.dto.CreatedUserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class CreatedUserDtoToUser implements Converter<CreatedUserDto, User> {
    private final DateTimeFormatter dateFormatter;

    @Override
    public User convert(CreatedUserDto source) {
        String name = source.isEmptyName() ? source.getLogin() : source.getName();
        LocalDate birthDate = LocalDate.parse(source.getBirthday(), dateFormatter);
        return new User(source.getId(), source.getEmail(), source.getLogin(), name, birthDate);
    }
}
