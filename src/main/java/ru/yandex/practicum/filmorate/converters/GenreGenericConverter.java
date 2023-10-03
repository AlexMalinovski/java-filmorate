package ru.yandex.practicum.filmorate.converters;

import ru.yandex.practicum.filmorate.dto.CreatedGenreDto;
import ru.yandex.practicum.filmorate.models.Genre;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;

public class GenreGenericConverter extends AbstractGenericConverter {
    @Override
    protected Map<ConvertiblePair, Function<Object, Object>> setSupportConversions() {
        return Map.ofEntries(
                new AbstractMap.SimpleEntry<>(
                        new ConvertiblePair(Genre.class, CreatedGenreDto.class), this::convertGenreToCreatedGenreDto)
        );
    }

    private CreatedGenreDto convertGenreToCreatedGenreDto(Object genreObj) {
        Genre genre = (Genre) genreObj;
        return CreatedGenreDto.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }
}
