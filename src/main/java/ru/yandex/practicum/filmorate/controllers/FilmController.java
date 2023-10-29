package ru.yandex.practicum.filmorate.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.CreatedFilmDto;
import ru.yandex.practicum.filmorate.dto.CreatedGenreDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UpdateFilmDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.models.FilmSort;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.services.SearchService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@RestController
public class FilmController {
    private final FilmService filmService;
    private final SearchService searchService;
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
     *
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
     *
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
     *
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
     *
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

    @GetMapping(path = "/films/director/{directorId}")
    public ResponseEntity<List<CreatedFilmDto>> getFilmsByDirector(@RequestParam String sortBy,
                                                                   @PathVariable long directorId) {
        FilmSort sort;
        try {
            sort = FilmSort.valueOf(sortBy.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Некорректные параметры URL");
        }
        if (directorId <= 0) {
            throw new NotFoundException("Некорректные параметры URL");
        }

        List<CreatedFilmDto> filmsDto = filmService.getFilmsByDirector(directorId, sort)
                .stream()
                .map(f -> conversionService.convert(f, CreatedFilmDto.class))
                .collect(Collectors.toList());
        log.debug("Получен список фильмов режиссера с id = {}, отсортированный по {}", directorId, sortBy);
        return ResponseEntity.ok(filmsDto);
    }

    @GetMapping(path = "/films/search")
    public ResponseEntity<List<CreatedFilmDto>> getFilmsBySearch(@RequestParam(value = "query", required = false, defaultValue = "popular") String str,
                                                                 @RequestParam(value = "by", required = false, defaultValue = "nothing") String by) {

        List<CreatedFilmDto> filmsDto;
        if (str.equals("popular")) {
            filmsDto = filmService.getMostPopularFilms(10)
                    .stream()
                    .map(f -> conversionService.convert(f, CreatedFilmDto.class))
                    .collect(Collectors.toList());
            log.debug("Получены 10 популярных фильмов");
            return ResponseEntity.ok(filmsDto);
        }

        filmsDto = searchService.getFilmsBySearchParams(by, str).stream()
                .map(f -> conversionService.convert(f, CreatedFilmDto.class))
                .collect(Collectors.toList());
        log.debug("Выполнен поиск фильмов по {} c запросом {}", by, str);
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
                .orElseThrow(() -> new NotFoundException("Не найден жанр с id:" + id));
    }

    @GetMapping("/mpa")
    public ResponseEntity<List<FilmRating>> getFilmRatings() {
        return ResponseEntity.ok(List.of(FilmRating.values()));
    }

    @GetMapping("/mpa/{id}")
    public ResponseEntity<FilmRating> getFilmRatingById(@PathVariable int id) {
        return FilmRating.getByIndex(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Отсутствует рейтинг с id:" + id));
    }
}
