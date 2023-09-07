package ru.yandex.practicum.filmorate.converters;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.configs.AppProperties;
import ru.yandex.practicum.filmorate.dto.CreatedFilmDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class FilmGenericConverter extends AbstractGenericConverter {
    protected final AppProperties appProperties;

    @Override
    protected Map<ConvertiblePair, Function<Object, Object>> setSupportConversions() {
        return Map.ofEntries(
                new AbstractMap.SimpleEntry<>(
                        new ConvertiblePair(UpdateFilmDto.class, Film.class),
                        this::convertUpdateFilmDtoToFilm),
                new AbstractMap.SimpleEntry<>(
                        new ConvertiblePair(FilmDto.class, Film.class),
                        this::convertFilmDtoToFilm),
                new AbstractMap.SimpleEntry<>(
                        new ConvertiblePair(Film.class, CreatedFilmDto.class),
                        this::convertFilmToCreatedFilmDto));
    }

    private Film convertUpdateFilmDtoToFilm(Object updateFilmDto) {
        UpdateFilmDto dto = (UpdateFilmDto) updateFilmDto;
        return Film.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(LocalDate.parse(dto.getReleaseDate(), appProperties.getDefaultDateFormatter()))
                .duration(Duration.ofMinutes(dto.getDuration()))
                .build();
    }

    private Film convertFilmDtoToFilm(Object filmDto) {
        FilmDto dto = (FilmDto) filmDto;
        return Film.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(LocalDate.parse(dto.getReleaseDate(), appProperties.getDefaultDateFormatter()))
                .duration(Duration.ofMinutes(dto.getDuration()))
                .build();
    }

    private CreatedFilmDto convertFilmToCreatedFilmDto(Object filmObj) {
        Film film = (Film) filmObj;
        return CreatedFilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate().format(appProperties.getDefaultDateFormatter()))
                .duration(film.getDuration().toMinutes())
                .numLikes(film.getNumOfLikes())
                .build();
    }
}
