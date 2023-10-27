package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.validators.FilmRelease;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@RequiredArgsConstructor
@Validated
public final class UpdateFilmDto {
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

    @NotNull(message = "Рейтинг не указан")
    @Valid
    private final LongIdDto mpa;

    private final List<@Valid LongIdDto> directors;

    private final List<@Valid LongIdDto> genres;
}
