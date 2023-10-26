package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@Builder
@RequiredArgsConstructor
@Validated
public final class UpdateDirectorDto {
    @Positive(message = "Id должен быть положительным числом")
    private final long id;

    @NotBlank(message = "Имя не может быть пустым")
    private final String name;
}
