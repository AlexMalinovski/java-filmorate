package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.validators.Birthday;
import ru.yandex.practicum.filmorate.validators.RequiredEmail;
import ru.yandex.practicum.filmorate.validators.RequiredLogin;

import javax.validation.constraints.Positive;

/**
 * Controllers-DTO для отображения пользователей
 */
@Data
@Builder
@RequiredArgsConstructor
public final class CreatedUserDto {
    @Positive(message = "Id должен быть положительным числом")
    private final long id;

    @RequiredEmail
    private final String email;

    @RequiredLogin
    private final String login;
    private final String name;

    @Birthday
    private final String birthday;
    private final int numFriends;
}
