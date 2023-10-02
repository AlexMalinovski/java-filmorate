package ru.yandex.practicum.filmorate.converters;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.configs.AppProperties;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Set<Genre> genres = Optional.ofNullable(dto.getGenres())
                .orElse(new ArrayList<>())
                .stream()
                .filter(Objects::nonNull)
                .map(d -> Genre.builder().id(d.getId()).build())
                .collect(Collectors.toSet());

        return Film.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(LocalDate.parse(dto.getReleaseDate(), appProperties.getDefaultDateFormatter()))
                .duration(Duration.ofMinutes(dto.getDuration()))
                .rating(FilmRating.getByIndex((int) dto.getMpa().getId())
                        .orElseThrow(()-> new IllegalArgumentException("Некорректный индекс элемента FilmRating")))
                .genres(genres)
                .build();
    }

    private Film convertFilmDtoToFilm(Object filmDto) {
        FilmDto dto = (FilmDto) filmDto;
        Set<Genre> genres = Optional.ofNullable(dto.getGenres())
                .orElse(new ArrayList<>())
                .stream()
                .filter(Objects::nonNull)
                .map(d -> Genre.builder().id(d.getId()).build())
                .collect(Collectors.toSet());

        return Film.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .releaseDate(LocalDate.parse(dto.getReleaseDate(), appProperties.getDefaultDateFormatter()))
                .duration(Duration.ofMinutes(dto.getDuration()))
                .rating(FilmRating.getByIndex((int) dto.getMpa().getId())
                        .orElseThrow(()-> new IllegalArgumentException("Некорректный индекс элемента FilmRating")))
                .genres(genres)
                .build();
    }

    private CreatedFilmDto convertFilmToCreatedFilmDto(Object filmObj) {
        Film film = (Film) filmObj;
        List<CreatedGenreDto> genres = film.getGenres().stream()
                .map(g -> CreatedGenreDto.builder().id(g.getId()).name(g.getName()).build())
                .collect(Collectors.toList());
        return CreatedFilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate().format(appProperties.getDefaultDateFormatter()))
                .duration(film.getDuration().toMinutes())
                .numLikes(film.getNumOfLikes())
                .mpa(film.getRating())
                .genres(genres)
                .build();
    }
}
