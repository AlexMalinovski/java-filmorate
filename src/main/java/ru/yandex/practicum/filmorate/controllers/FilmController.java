package ru.yandex.practicum.filmorate.controllers;


import io.swagger.v3.oas.annotations.tags.Tag;
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
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.models.FilmSort;
import ru.yandex.practicum.filmorate.models.Operation;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.services.SearchService;
import ru.yandex.practicum.filmorate.services.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Фильмы", description = "API для работы с фильмами")
@RequiredArgsConstructor
@Slf4j
@RestController
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;
    private final SearchService searchService;
    private final ConversionService conversionService;

    /**
     * Получение всех фильмов
     * @return List<CreatedFilmDto>
     */
    @GetMapping("/films")
    @io.swagger.v3.oas.annotations.Operation(summary = "Получение всех фильмов")
    public ResponseEntity<List<CreatedFilmDto>> getFilms() {
        List<CreatedFilmDto> filmsDto = filmService.getFilms()
                .stream()
                .map(film -> conversionService.convert(film, CreatedFilmDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filmsDto);
    }

    /**
     * Добавление фильма
     * @param filmDto FilmDto
     * @return CreatedFilmDto
     */
    @PostMapping("/films")
    @io.swagger.v3.oas.annotations.Operation(summary = "Добавление фильма")
    public ResponseEntity<CreatedFilmDto> createFilm(@Valid @RequestBody FilmDto filmDto) {
        Film film = Optional.ofNullable(conversionService.convert(filmDto, Film.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации FilmDto->Film. Метод вернул null."));
        Film createdFilm = filmService.createFilm(film);
        log.debug("Добавлен новый фильм с id={}", createdFilm.getId());
        return ResponseEntity.ok(conversionService.convert(createdFilm, CreatedFilmDto.class));
    }

    /**
     * Обновление фильма
     * @param filmDto UpdateFilmDto
     * @return CreatedFilmDto
     */
    @PutMapping("/films")
    @io.swagger.v3.oas.annotations.Operation(summary = "Обновление фильма")
    public ResponseEntity<CreatedFilmDto> updateCreatedFilm(@Valid @RequestBody UpdateFilmDto filmDto) {
        final Film filmUpdates = Optional.ofNullable(conversionService.convert(filmDto, Film.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации CreatedFilmDto->Film. Метод вернул null."));
        final Film result = filmService.updateFilm(filmUpdates)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        log.debug("Изменён фильм с id={}", filmUpdates.getId());
        return ResponseEntity.ok(conversionService.convert(result, CreatedFilmDto.class));
    }

    /**
     * Обновление фильма
     * @param id id фильма
     * @param filmDto FilmDto
     * @return CreatedFilmDto
     */
    @PutMapping(path = "/films/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Обновление фильма")
    public ResponseEntity<CreatedFilmDto> updateFilmById(@PathVariable final long id,
                                                         @Valid @RequestBody FilmDto filmDto) {
        if (id <= 0) {
            throw new NotFoundException("Id фильма должен быть положительным числом");
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
    @io.swagger.v3.oas.annotations.Operation(summary = "Получение фильма по id")
    public ResponseEntity<CreatedFilmDto> getFilmById(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Id фильма должен быть положительным числом");
        }
        return filmService.getFilmById(id)
                .map(film -> conversionService.convert(film, CreatedFilmDto.class))
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
    @io.swagger.v3.oas.annotations.Operation(summary = "Поставить лайк")
    public ResponseEntity<CreatedFilmDto> likeFilm(@PathVariable long filmId,
                                                   @PathVariable long userId) {
        if (filmId <= 0) {
            throw new NotFoundException("Id фильма должен быть положительным числом");
        }
        if (userId <= 0) {
            throw new NotFoundException("Id пользователя должен быть положительным числом");
        }
        Film likedFilm = filmService.likeFilm(filmId, userId);
        log.debug("Пользователь id={} лайкнул фильм id={}", userId, filmId);
        userService.addEvent(userId, filmId, EventType.LIKE, Operation.ADD);
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
    @io.swagger.v3.oas.annotations.Operation(summary = "Удалить лайк")
    public ResponseEntity<CreatedFilmDto> unlikeFilm(@PathVariable long filmId,
                                                     @PathVariable long userId) {
        if (filmId <= 0) {
            throw new NotFoundException("Id фильма должен быть положительным числом");
        }
        if (userId <= 0) {
            throw new NotFoundException("Id пользователя должен быть положительным числом");
        }
        Film unlikedFilm = filmService.unlikeFilm(filmId, userId);
        log.debug("Пользователь id={} дизлайкнул фильм id={}", userId, filmId);
        userService.addEvent(userId, filmId, EventType.LIKE, Operation.REMOVE);
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
    @io.swagger.v3.oas.annotations.Operation(summary = "Топ фильмов по лайкам")
    public ResponseEntity<List<CreatedFilmDto>> getMostPopularFilms(@RequestParam(defaultValue = "10") int count,
                                                                    @RequestParam(required = false) Long genreId,
                                                                    @RequestParam(required = false) Integer year) {
        if (count <= 0) {
            throw new NotFoundException("Значение параметра count должно быть положительным");
        }
        List<CreatedFilmDto> filmsDto = filmService.getMostPopularFilms(count, genreId, year)
                .stream()
                .map(film -> conversionService.convert(film, CreatedFilmDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filmsDto);
    }

    /**
     * Возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска.
     * @param sortBy способ сортировки
     * @param directorId id режиссера
     * @return List<CreatedFilmDto>
     */
    @GetMapping(path = "/films/director/{directorId}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Фильмы режиссёра")
    public ResponseEntity<List<CreatedFilmDto>> getFilmsByDirector(@RequestParam String sortBy,
                                                                   @PathVariable long directorId) {
        FilmSort sort;
        try {
            sort = FilmSort.valueOf(sortBy.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Недопустимое значение способа сортировки");
        }
        if (directorId <= 0) {
            throw new NotFoundException("Id режиссера должен быть положительным");
        }

        List<CreatedFilmDto> filmsDto = filmService.getFilmsByDirector(directorId, sort)
                .stream()
                .map(film -> conversionService.convert(film, CreatedFilmDto.class))
                .collect(Collectors.toList());
        log.debug("Получен список фильмов режиссера с id = {}, отсортированный по {}", directorId, sortBy);
        return ResponseEntity.ok(filmsDto);
    }

    /**
     * Возвращает список фильмов, отсортированных по популярности.
     * @param str текст для поиска
     * @param by может принимать значения director (поиск по режиссёру), title (поиск по названию),
     * либо оба значения через запятую при поиске одновременно и по режиссеру и по названию.
     * @return <List<CreatedFilmDto>
     */
    @GetMapping(path = "/films/search")
    @io.swagger.v3.oas.annotations.Operation(summary = "Поиск фильмов")
    public ResponseEntity<List<CreatedFilmDto>> getFilmsBySearch(@RequestParam(value = "query", required = false, defaultValue = "popular") String str,
                                                                 @RequestParam(value = "by", required = false, defaultValue = "nothing") String by) {

        List<CreatedFilmDto> filmsDto;
        if (str.equals("popular")) {
            filmsDto = filmService.getMostPopularFilms(10, null, null)
                    .stream()
                    .map(film -> conversionService.convert(film, CreatedFilmDto.class))
                    .collect(Collectors.toList());
            log.debug("Получены 10 популярных фильмов");
        } else {
            filmsDto = searchService.getFilmsBySearchParams(by, str).stream()
                    .map(film -> conversionService.convert(film, CreatedFilmDto.class))
                    .collect(Collectors.toList());
            log.debug("Выполнен поиск фильмов по {} c запросом {}", by, str);
        }
        return ResponseEntity.ok(filmsDto);
    }

    /**
     * Возвращает список жанров
     * @return List<CreatedGenreDto>
     */
    @GetMapping("/genres")
    @io.swagger.v3.oas.annotations.Operation(summary = "Список жанров")
    public ResponseEntity<List<CreatedGenreDto>> getGenres() {
        List<CreatedGenreDto> genres = filmService.getGenres()
                .stream()
                .map(genre -> conversionService.convert(genre, CreatedGenreDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(genres);
    }

    /**
     * Возвращает жанр по его id
     * @param id id жанра
     * @return CreatedGenreDto
     */
    @GetMapping("/genres/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Получение жанра по его id")
    public ResponseEntity<CreatedGenreDto> getGenreById(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Id жанра должен быть положительным числом");
        }
        return filmService.getGenreById(id)
                .map(genre -> conversionService.convert(genre, CreatedGenreDto.class))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Не найден жанр с id:" + id));
    }

    /**
     * Возвращает список возрастных рейтиногов
     * @return List<FilmRating>
     */
    @GetMapping("/mpa")
    @io.swagger.v3.oas.annotations.Operation(summary = "Список возрастных рейтиногов")
    public ResponseEntity<List<FilmRating>> getFilmRatings() {
        return ResponseEntity.ok(List.of(FilmRating.values()));
    }

    /**
     * Возвращает рейтинг по его id
     * @param id id рейтинга
     * @return FilmRating
     */
    @GetMapping("/mpa/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Получение рейтинга по его id")
    public ResponseEntity<FilmRating> getFilmRatingById(@PathVariable int id) {
        return FilmRating.getByIndex(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Отсутствует рейтинг с id:" + id));
    }

    /**
     * Возвращает общие с другом фильмы с сортировкой по их популярности.
     * @param userId идентификатор пользователя, запрашивающего информацию
     * @param friendId идентификатор пользователя, с которым необходимо сравнить список фильмов
     * @return List<CreatedFilmDto>
     */
    @GetMapping("/films/common")
    @io.swagger.v3.oas.annotations.Operation(summary = "Получение общих с другом фильмы с сортировкой по популярности")
    public ResponseEntity<List<CreatedFilmDto>> getCommonFilms(@RequestParam long userId,
                                                               @RequestParam long friendId) {
        if (friendId <= 0 || userId <= 0) {
            throw new NotFoundException("Id пользователей должны быть положительными числами");
        }

        List<CreatedFilmDto> filmsDto = filmService.getCommonFilms(userId, friendId)
                .stream()
                .map(film -> conversionService.convert(film, CreatedFilmDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filmsDto);
    }

    /**
     * Удаляет фильм по его id
     * @param id id фильма
     * @return CreatedFilmDto
     */
    @DeleteMapping(path = "/films/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Удаление фильма")
    public ResponseEntity<CreatedFilmDto> deleteFilmById(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Id фильма должен быть положительным числом");
        }
        Film deletedFilm = filmService.deleteFilmById(id);
        log.debug("фильм id={} удален", id);
        return ResponseEntity.ok(conversionService.convert(deletedFilm, CreatedFilmDto.class));
    }
}
