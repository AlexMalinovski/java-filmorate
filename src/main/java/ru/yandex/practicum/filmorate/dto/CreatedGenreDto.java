package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Builder
@RequiredArgsConstructor
public class CreatedGenreDto {
    @Positive(message = "Id жанра должен быть положительным числом")
    private final long id;

    @NotBlank(message = "Название жанра не может быть пустым")
    private final String name;
}
