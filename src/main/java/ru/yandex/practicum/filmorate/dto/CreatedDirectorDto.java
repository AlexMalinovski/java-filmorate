package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * Controllers-DTO для отображения режиссеров
 */

@Data
@Builder
@RequiredArgsConstructor
public final class CreatedDirectorDto {
    @Positive(message = "Id должен быть положительным числом")
    private final long id;

    @NotBlank(message = "Имя не может быть пустым")
    private final String name;
}
