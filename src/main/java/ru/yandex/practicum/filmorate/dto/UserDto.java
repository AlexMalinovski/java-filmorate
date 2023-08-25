package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.validators.Birthday;
import ru.yandex.practicum.filmorate.validators.RequiredEmail;
import ru.yandex.practicum.filmorate.validators.RequiredLogin;

@Data
public class UserDto {
    @RequiredEmail
    private final String email;

    @RequiredLogin
    private final String login;
    private final String name;

    @Birthday
    private final String birthday;
}
