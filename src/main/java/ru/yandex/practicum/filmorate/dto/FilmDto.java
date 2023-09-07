package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validators.FilmRelease;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * Controllers-DTO для создания или редактирования (id должен быть передан через PathVariable) фильмов.
 */
@Data
@Builder
@RequiredArgsConstructor
public final class FilmDto {
    @NotBlank(message = "Название не может быть пустым")
    private final String name;

    @Length(max = 200, message = "Максимальная длина описания - 200 символов")
    private final String description;

    @FilmRelease
    private final String releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private final long duration;
}
