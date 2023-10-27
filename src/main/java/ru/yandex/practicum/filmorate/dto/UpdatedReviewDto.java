package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
@RequiredArgsConstructor
public class UpdatedReviewDto {
    @Positive(message = "Id отзыва должен быть положительным числом")
    private final long reviewId;

    @NotEmpty
    @Length(min = 1, max = 200, message = "Текст отзыва должен иметь длину от 1-200 символов")
    private final String content;

    @NotNull
    private final Boolean isPositive;
}
