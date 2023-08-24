package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import ru.yandex.practicum.filmorate.validators.Birthday;
import ru.yandex.practicum.filmorate.validators.RequiredEmail;
import ru.yandex.practicum.filmorate.validators.RequiredLogin;

@Data
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

    public boolean isEmptyName() {
        return name == null || name.isBlank();
    }
}
