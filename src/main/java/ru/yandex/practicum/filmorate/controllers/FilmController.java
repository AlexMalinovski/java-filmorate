package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.CreatedFilmDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final ConversionService conversionService;
    private final Map<Long, Film> films = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(1L);

    @GetMapping
    public ResponseEntity<List<CreatedFilmDto>> getFilms() {
        List<CreatedFilmDto> filmsDto = films.values()
                .stream()
                .map(f -> conversionService.convert(f, CreatedFilmDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(filmsDto);
    }

    @PostMapping
    public ResponseEntity<CreatedFilmDto> createFilm(@Valid @RequestBody FilmDto filmDto) {
        Film film = Optional.ofNullable(conversionService.convert(filmDto, Film.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации FilmDto->Film. Метод вернул null."));
        film.setId(currentId.getAndIncrement());
        films.put(film.getId(), film);
        log.debug("Добавлен новый фильм с id={}", film.getId());
        return ResponseEntity.ok(conversionService.convert(film, CreatedFilmDto.class));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<CreatedFilmDto> updateFilmById(@PathVariable final long id,
                                                     @Valid @RequestBody FilmDto filmDto) {
        final Film filmUpdates = Optional.ofNullable(conversionService.convert(filmDto, Film.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации FilmDto->Film. Метод вернул null."));
        final Film result = Optional.ofNullable(films.computeIfPresent(id, (k, v) -> filmUpdates))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        log.debug("Изменён фильм с id={}", id);
        return ResponseEntity.ok(conversionService.convert(result, CreatedFilmDto.class));
    }

    @PutMapping
    public ResponseEntity<CreatedFilmDto> updateCreatedFilm(@Valid @RequestBody CreatedFilmDto filmDto) {
        final Film filmUpdates = Optional.ofNullable(conversionService.convert(filmDto, Film.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации CreatedFilmDto->Film. Метод вернул null."));
        final Film result = Optional.ofNullable(films.computeIfPresent(filmUpdates.getId(), (k, v) -> filmUpdates))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        log.debug("Изменён фильм с id={}", filmUpdates.getId());
        return ResponseEntity.ok(conversionService.convert(result, CreatedFilmDto.class));
    }
}
