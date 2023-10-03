package ru.yandex.practicum.filmorate.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FilmRating {
    G(1, "G", "Без возрастных ограничений"),
    PG(2, "PG", "Детям рекомендуется смотреть фильм с родителями"),
    PG_13(3, "PG-13", "Детям до 13 лет просмотр не желателен"),
    R(4, "R", "Лицам до 17 лет просмотр только в присутствии взрослого"),
    NC_17(5, "NC-17", "Лицам до 18 лет просмотр запрещён");

    @JsonProperty("id")
    private final int index;
    @JsonProperty("name")
    private final String title;
    private final String description;

    FilmRating(int index, String title, String description) {
        this.index = index;
        this.title = title;
        this.description = description;
    }

    public static Optional<FilmRating> getByIndex(final int index) {
        return Arrays.stream(FilmRating.values())
                .filter(r -> r.getIndex() == index)
                .findAny();
    }
}
