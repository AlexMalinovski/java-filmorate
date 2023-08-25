package ru.yandex.practicum.filmorate.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.GenericConverter;
import ru.yandex.practicum.filmorate.configs.AppProperties;
import ru.yandex.practicum.filmorate.dto.CreatedUserDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class UserGenericConverter extends AbstractGenericConverter {
    private final AppProperties appProperties;
    @Override
    protected Map<ConvertiblePair, Function<Object, Object>> setSupportConversions() {
        return Map.ofEntries(
                new AbstractMap.SimpleEntry<>(
                        new GenericConverter.ConvertiblePair(CreatedUserDto.class, User.class),
                        this::convertCreatedUserDtoToUser),
                new AbstractMap.SimpleEntry<>(
                        new GenericConverter.ConvertiblePair(UserDto.class, User.class),
                        this::convertUserDtoToUser),
                new AbstractMap.SimpleEntry<>(
                        new GenericConverter.ConvertiblePair(User.class, CreatedUserDto.class),
                        this::convertUserToCreatedUserDto));
    }

    private User convertCreatedUserDtoToUser(Object createdUserDto) {
        CreatedUserDto dto = (CreatedUserDto) createdUserDto;
        String name = (dto.getName() == null || dto.getName().isBlank()) ? dto.getLogin() : dto.getName();
        return User.builder()
                .id(dto.getId())
                .email(dto.getEmail())
                .login(dto.getLogin())
                .name(name)
                .birthday(LocalDate.parse(dto.getBirthday(), appProperties.getDefaultDateFormatter()))
                .build();
    }

    private User convertUserDtoToUser(Object userDto) {
        UserDto dto = (UserDto) userDto;
        String name = (dto.getName() == null || dto.getName().isBlank()) ? dto.getLogin() : dto.getName();
        return User.builder()
                .email(dto.getEmail())
                .login(dto.getLogin())
                .name(name)
                .birthday(LocalDate.parse(dto.getBirthday(), appProperties.getDefaultDateFormatter()))
                .build();
    }

    private CreatedUserDto convertUserToCreatedUserDto(Object userObj) {
        User user = (User) userObj;
        return CreatedUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday().format(appProperties.getDefaultDateFormatter()))
                .build();
    }
}