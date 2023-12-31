package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.validators.Birthday;
import ru.yandex.practicum.filmorate.validators.RequiredEmail;
import ru.yandex.practicum.filmorate.validators.RequiredLogin;

/**
 * Controllers-DTO для создания или редактирования (id должен быть передан через PathVariable) пользователей
 */
@Data
@Builder
@RequiredArgsConstructor
public class UserDto {
    @RequiredEmail
    private final String email;

    @RequiredLogin
    private final String login;
    private final String name;

    @Birthday
    private final String birthday;
}
