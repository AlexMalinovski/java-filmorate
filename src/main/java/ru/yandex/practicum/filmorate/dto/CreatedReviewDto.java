package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Positive;

@Data
@Builder
@RequiredArgsConstructor
public class CreatedReviewDto {
    @Positive(message = "Id отзыва должен быть положительным числом")
    private final long reviewId;

    @Length(min = 1, max = 200, message = "Текст отзыва должен иметь длину от 1-200 символов")
    private final String content;

    @JsonProperty
    private final boolean isPositive;

    @Positive(message = "Id автора должен быть положительным числом")
    private final long userId;

    @Positive(message = "Id фильма должен быть положительным числом")
    private final long filmId;

    private final long useful;
}
