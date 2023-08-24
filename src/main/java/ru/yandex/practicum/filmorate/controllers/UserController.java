package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.CreatedUserDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final ConversionService conversionService;
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong currentId = new AtomicLong(1L);

    @GetMapping
    public ResponseEntity<List<CreatedUserDto>> getFilms() {
        List<CreatedUserDto> userDto = users.values()
                .stream()
                .map(f -> conversionService.convert(f, CreatedUserDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDto);
    }

    @PostMapping
    public ResponseEntity<CreatedUserDto> createUser(@Valid @RequestBody UserDto userDto) {
        User user = Optional.ofNullable(conversionService.convert(userDto, User.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации UserDto->User. Метод вернул null."));
        user.setId(currentId.getAndIncrement());
        users.put(user.getId(), user);
        log.debug("Добавлен новый пользователь с id={}", user.getId());
        return ResponseEntity.ok(conversionService.convert(user, CreatedUserDto.class));
    }

    @PutMapping
    public ResponseEntity<CreatedUserDto> updateUser(@Valid @RequestBody CreatedUserDto userDto) {
        final User userUpdates = Optional.ofNullable(conversionService.convert(userDto, User.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации CreatedUserDto->User. Метод вернул null."));
        final User result = Optional.ofNullable(users.computeIfPresent(userUpdates.getId(), (k, v) -> userUpdates))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.debug("Изменён пользователь с id={}", userUpdates.getId());
        return ResponseEntity.ok(conversionService.convert(result, CreatedUserDto.class));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<CreatedUserDto> updateUser(@PathVariable final long id,
                                                     @Valid @RequestBody UserDto userDto) {
        final User userUpdates = Optional.ofNullable(conversionService.convert(userDto, User.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации UserDto->User. Метод вернул null."));
        User result = Optional.ofNullable(users.computeIfPresent(id, (k, v) -> userUpdates))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.debug("Изменён пользователь с id={}", id);
        return ResponseEntity.ok(conversionService.convert(result, CreatedUserDto.class));
    }
}
