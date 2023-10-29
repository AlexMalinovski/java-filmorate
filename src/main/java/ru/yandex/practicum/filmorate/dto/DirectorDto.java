package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

/**
 * Controllers-DTO для создания или редактирования (id должен быть передан через PathVariable) режиссеров.
 */
@Data
@Builder
@RequiredArgsConstructor
public class DirectorDto {
    @Positive(message = "Id должен быть положительным числом")
    private final long id;

    @NotBlank(message = "Имя не может быть пустым")
    private final String name;
}
