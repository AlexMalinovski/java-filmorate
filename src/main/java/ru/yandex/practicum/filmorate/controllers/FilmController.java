package ru.yandex.practicum.filmorate.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.CreatedFilmDto;
import ru.yandex.practicum.filmorate.dto.CreatedGenreDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@RestController
public class FilmController {
    private final FilmService filmService;
    private final ConversionService conversionService;

    @GetMapping("/films")
    public ResponseEntity<List<CreatedFilmDto>> getFilms() {
        List<CreatedFilmDto> filmsDto = filmService.getFilms()
                .stream()
                .map(f -> conversionService.convert(f, CreatedFilmDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filmsDto);
    }

    @PostMapping("/films")
    public ResponseEntity<CreatedFilmDto> createFilm(@Valid @RequestBody FilmDto filmDto) {
        Film film = Optional.ofNullable(conversionService.convert(filmDto, Film.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации FilmDto->Film. Метод вернул null."));
        Film createdFilm = filmService.createFilm(film);
        log.debug("Добавлен новый фильм с id={}", createdFilm.getId());
        return ResponseEntity.ok(conversionService.convert(createdFilm, CreatedFilmDto.class));
    }

    @PutMapping("/films")
    public ResponseEntity<CreatedFilmDto> updateCreatedFilm(@Valid @RequestBody UpdateFilmDto filmDto) {
        final Film filmUpdates = Optional.ofNullable(conversionService.convert(filmDto, Film.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации CreatedFilmDto->Film. Метод вернул null."));
        final Film result = filmService.updateFilm(filmUpdates)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        log.debug("Изменён фильм с id={}", filmUpdates.getId());
        return ResponseEntity.ok(conversionService.convert(result, CreatedFilmDto.class));
    }

    @PutMapping(path = "/films/{id}")
    public ResponseEntity<CreatedFilmDto> updateFilmById(@PathVariable final long id,
                                                     @Valid @RequestBody FilmDto filmDto) {
        if (id <= 0) {
            throw new NotFoundException("Некорректные параметры URL");
        }
        final Film filmUpdates = Optional.ofNullable(conversionService.convert(filmDto, Film.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации FilmDto->Film. Метод вернул null."));
        final Film result = filmService.updateFilm(filmUpdates)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        log.debug("Изменён фильм с id={}", id);
        return ResponseEntity.ok(conversionService.convert(result, CreatedFilmDto.class));
    }

    /**
     * Получить фильм по уникальному идентификатору
     * @param id Идентификатор фильма
     * @return CreatedFilmDto
     */
    @GetMapping(path = "/films/{id}")
    public ResponseEntity<CreatedFilmDto> getFilmById(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Некорректные параметры URL");
        }
        return filmService.getFilmById(id)
                .map(f -> conversionService.convert(f, CreatedFilmDto.class))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Не найден фильм с id:" + id));
    }

    /**
     * Пользователь ставит лайк фильму
     * @param filmId id фильма
     * @param userId id пользователя
     * @return CreatedFilmDto
     */
    @PutMapping(path = "/films/{filmId}/like/{userId}")
    public ResponseEntity<CreatedFilmDto> likeFilm(@PathVariable long filmId,
                                                   @PathVariable long userId) {
        if (filmId <= 0 || userId <= 0) {
            throw new NotFoundException("Некорректные параметры URL");
        }
        Film likedFilm = filmService.likeFilm(filmId, userId);
        log.debug("Пользователь id={} лайкнул фильм id={}", userId, filmId);
        return ResponseEntity.ok(conversionService.convert(likedFilm, CreatedFilmDto.class));
    }

    /**
     * Пользователь удаляет лайк фильму
     * @param filmId id фильма
     * @param userId id пользователя
     * @return CreatedFilmDto
     */
    @DeleteMapping(path = "/films/{filmId}/like/{userId}")
    public ResponseEntity<CreatedFilmDto> unlikeFilm(@PathVariable long filmId,
                                                   @PathVariable long userId) {
        if (filmId <= 0 || userId <= 0) {
            throw new NotFoundException("Некорректные параметры URL");
        }
        Film unlikedFilm = filmService.unlikeFilm(filmId, userId);
        log.debug("Пользователь id={} дизлайкнул фильм id={}", userId, filmId);
        return ResponseEntity.ok(conversionService.convert(unlikedFilm, CreatedFilmDto.class));
    }

    /**
     * Возвращает список из первых count фильмов по количеству лайков.
     * Если значение параметра count не задано, вернёт первые 10.
     * @param count количество фильмов
     * @return список CreatedFilmDto
     */
    @GetMapping(path = "/films/popular")
    public ResponseEntity<List<CreatedFilmDto>> getMostPopularFilms(@RequestParam(defaultValue = "10") int count) {
        if (count <= 0) {
            throw new NotFoundException("Значение параметра count должно быть положительным");
        }
        List<CreatedFilmDto> filmsDto = filmService.getMostPopularFilms(count)
                .stream()
                .map(f -> conversionService.convert(f, CreatedFilmDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filmsDto);
    }

    @GetMapping("/genres")
    public ResponseEntity<List<CreatedGenreDto>> getGenres() {
        List<CreatedGenreDto> genres = filmService.getGenres()
                .stream()
                .map(g -> conversionService.convert(g, CreatedGenreDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(genres);
    }

    @GetMapping("/genres/{id}")
    public ResponseEntity<CreatedGenreDto> getGenreById(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Некорректные параметры URL");
        }
        return filmService.getGenreById(id)
                .map(g -> conversionService.convert(g, CreatedGenreDto.class))
                .map(ResponseEntity::ok)
                .orElseThrow(() ->  new NotFoundException("Не найден жанр с id:" + id));
    }

    @GetMapping("/mpa")
    public ResponseEntity<List<FilmRating>> getFilmRatings() {
        return  ResponseEntity.ok(List.of(FilmRating.values()));
    }

    @GetMapping("/mpa/{id}")
    public ResponseEntity<FilmRating> getFilmRatingById(@PathVariable int id) {
        return FilmRating.getByIndex(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Отсутствует рейтинг с id:" + id));
    }
}
