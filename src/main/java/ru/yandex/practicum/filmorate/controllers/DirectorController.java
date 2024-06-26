package ru.yandex.practicum.filmorate.controllers;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.CreatedDirectorDto;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.dto.UpdateDirectorDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.services.DirectorService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "Режиссёры", description = "API для работы с режиссёрами")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;
    private final ConversionService conversionService;

    /**
     * Список всех режиссёров
     * @return List<CreatedDirectorDto>
     */
    @GetMapping
    @Operation(summary = "Список всех режиссёров")
    public ResponseEntity<List<CreatedDirectorDto>> getDirectors() {
        List<CreatedDirectorDto> directorDto = directorService.getDirectors().stream()
                .map(director -> conversionService.convert(director, CreatedDirectorDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(directorDto);
    }

    /**
     * Создание режиссёра
     * @param directorDto DirectorDto
     * @return CreatedDirectorDto
     */
    @PostMapping
    @Operation(summary = "Создание режиссёра")
    public ResponseEntity<CreatedDirectorDto> createDirector(@Valid @RequestBody DirectorDto directorDto) {
        Director director = Optional.ofNullable(conversionService.convert(directorDto, Director.class))
                .orElseThrow(() -> new IllegalArgumentException("Ошибка конвертации DirectorDto->Director. Метод вернул null."));
        Director createdDirector = directorService.createDirector(director);
        log.debug("Добавлен новый режиссер с id={}", createdDirector.getId());
        return ResponseEntity.ok(conversionService.convert(createdDirector, CreatedDirectorDto.class));
    }

    /**
     * Изменение режиссёра
     * @param directorDto UpdateDirectorDto
     * @return CreatedDirectorDto
     */
    @PutMapping
    @Operation(summary = "Изменение режиссёра")
    public ResponseEntity<CreatedDirectorDto> updateDirector(@Valid @RequestBody UpdateDirectorDto directorDto) {
        final Director directorUpdates = Optional.ofNullable(conversionService.convert(directorDto, Director.class))
                .orElseThrow(() -> new IllegalArgumentException("Ошибка конвертации UpdateDirectorDto->Director. Метод вернул null."));
        final Director director = directorService.updateDirector(directorUpdates)
                .orElseThrow(() -> new NotFoundException("Режиссер не найден"));
        log.debug("Изменён режиссер с id={}", director.getId());
        return ResponseEntity.ok(conversionService.convert(director, CreatedDirectorDto.class));
    }

    /**
     * Получение режиссёра по id
     * @param id id режиссёра
     * @return CreatedDirectorDto
     */
    @GetMapping(path = "/{id}")
    @Operation(summary = "Получение режиссёра по id")
    public ResponseEntity<CreatedDirectorDto> getDirectorById(@PathVariable long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Некорректные параметры URL");
        }
        Director director = directorService.getDirectorById(id);
        log.debug("Получен режиссер с id = {}", id);
        return ResponseEntity.ok(conversionService.convert(director, CreatedDirectorDto.class));
    }

    /**
     * Удаление режиссёра
     * @param id id режиссёра
     */
    @DeleteMapping(path = "/{id}")
    @Operation(summary = "Удаление режиссёра")
    public void deleteDirectorById(@PathVariable long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Некорректные параметры URL");
        }
        directorService.deleteDirectorById(id);
        log.debug("Удален режиссер с id = {}", id);
    }
}
