package ru.yandex.practicum.filmorate.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.filmorate.dto.CreatedFilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class FilmToCreatedFilmDto implements Converter<Film, CreatedFilmDto> {
    private final DateTimeFormatter dateFormatter;

    @Override
    public CreatedFilmDto convert(Film source) {
        return new CreatedFilmDto(
                source.getId(),
                source.getName(),
                source.getDescription(),
                source.getReleaseDate().format(dateFormatter),
                source.getDuration().toMinutes());
    }
}
