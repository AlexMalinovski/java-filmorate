package ru.yandex.practicum.filmorate.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import ru.yandex.practicum.filmorate.dto.CreatedFilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public class CreatedFilmDtoToFilm implements Converter<CreatedFilmDto, Film> {
    private final DateTimeFormatter dateFormatter;

    @Override
    public Film convert(CreatedFilmDto source) {
        LocalDate releaseDate = LocalDate.parse(source.getReleaseDate(), dateFormatter);
        return new Film(source.getId(), source.getName(), source.getDescription(),
                releaseDate, Duration.ofMinutes(source.getDuration()));
    }
}
