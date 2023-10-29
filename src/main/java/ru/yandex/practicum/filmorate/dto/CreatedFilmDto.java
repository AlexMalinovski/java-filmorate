package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.validators.FilmRelease;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

/**
 * Controllers-DTO для отображения фильмов
 */
@Data
@Builder
@RequiredArgsConstructor
public final class CreatedFilmDto {
    @Positive(message = "Id должен быть положительным числом")
    private final long id;

    @NotBlank(message = "Название не может быть пустым")
    private final String name;

    @Length(max = 200, message = "Максимальная длина описания - 200 символов")
    private final String description;

    @FilmRelease
    private final String releaseDate;

    @Positive(message = "Продолжительность фильма должна быть положительной")
    private final long duration;
    private final int numLikes;
    private final FilmRating mpa;

    @Builder.Default
    private final List<CreatedDirectorDto> directors = new ArrayList<>();

    @Builder.Default
    private final List<CreatedGenreDto> genres = new ArrayList<>();
}
