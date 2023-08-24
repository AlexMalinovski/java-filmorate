package ru.yandex.practicum.filmorate.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class FilmDtoToFilm implements Converter<FilmDto, Film> {
    private final DateTimeFormatter dateFormatter;

    @Override
    public Film convert(FilmDto source) {
        LocalDate releaseDate = LocalDate.parse(source.getReleaseDate(), dateFormatter);
        return new Film(null, source.getName(), source.getDescription(),
                releaseDate, Duration.ofMinutes(source.getDuration()));
    }
}
