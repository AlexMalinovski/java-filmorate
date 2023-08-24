package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validators.FilmRelease;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
public final class FilmDto {
    @NotBlank(message = "Название не может быть пустым")
    private final String name;

    @Length(max = 200, message = "Максимальная длина описания - 200 символов")
    private final String description;

    @FilmRelease
    private final String releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private final int duration;
}
