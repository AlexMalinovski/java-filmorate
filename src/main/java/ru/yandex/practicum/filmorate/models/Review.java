package ru.yandex.practicum.filmorate.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Review {
    private Long id;
    private long userId;
    private long filmId;
    private String content;
    private boolean isPositive;
    private long useful;
}
