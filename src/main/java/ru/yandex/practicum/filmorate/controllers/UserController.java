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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.CreatedEventDto;
import ru.yandex.practicum.filmorate.dto.CreatedFilmDto;
import ru.yandex.practicum.filmorate.dto.CreatedUserDto;
import ru.yandex.practicum.filmorate.dto.UpdateUserDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.Operation;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.services.RecommendationService;
import ru.yandex.practicum.filmorate.services.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final ConversionService conversionService;
    private final RecommendationService recommendationService;

    /**
     * Получение списка всех пользователей
     * @return List<CreatedUserDto>
     */
    @GetMapping
    public ResponseEntity<List<CreatedUserDto>> getUsers() {
        List<CreatedUserDto> userDto = userService.getUsers()
                .stream()
                .map(user -> conversionService.convert(user, CreatedUserDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDto);
    }

    /**
     * Создание пользователя
     * @param userDto UserDto
     * @return CreatedUserDto
     */
    @PostMapping
    public ResponseEntity<CreatedUserDto> createUser(@Valid @RequestBody UserDto userDto) {
        User user = Optional.ofNullable(conversionService.convert(userDto, User.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации UserDto->User. Метод вернул null."));
        User createdUser = userService.createUser(user);
        log.debug("Добавлен новый пользователь с id={}", createdUser.getId());
        return ResponseEntity.ok(conversionService.convert(createdUser, CreatedUserDto.class));
    }

    /**
     * Обновление пользователя
     * @param userDto UpdateUserDto
     * @return CreatedUserDto
     */
    @PutMapping
    public ResponseEntity<CreatedUserDto> updateUser(@Valid @RequestBody UpdateUserDto userDto) {
        final User userUpdates = Optional.ofNullable(conversionService.convert(userDto, User.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации UpdateUserDto->User. Метод вернул null."));
        final User result = userService.updateUser(userUpdates)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.debug("Изменён пользователь с id={}", userUpdates.getId());
        return ResponseEntity.ok(conversionService.convert(result, CreatedUserDto.class));
    }

    /**
     * Обновление пользователя
     * @param id id пользователя
     * @param userDto UserDto
     * @return CreatedUserDto
     */
    @PutMapping(path = "/{id}")
    public ResponseEntity<CreatedUserDto> updateUserById(@PathVariable final long id,
                                                         @Valid @RequestBody UserDto userDto) {
        if (id <= 0) {
            throw new NotFoundException("Id пользователя должен быть положительным числом");
        }
        final User userUpdates = Optional.ofNullable(conversionService.convert(userDto, User.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации UserDto->User. Метод вернул null."));
        User result = userService.updateUser(userUpdates)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.debug("Изменён пользователь с id={}", id);
        return ResponseEntity.ok(conversionService.convert(result, CreatedUserDto.class));
    }

    /**
     * Получить пользователя по уникальному идентификатору
     * @param id Идентификатор пользователя
     * @return CreatedFilmDto
     */
    @GetMapping(path = "/{id}")
    public ResponseEntity<CreatedUserDto> getUserById(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Id пользователя должен быть положительным числом");
        }
        return userService.getUserById(id)
                .map(user -> conversionService.convert(user, CreatedUserDto.class))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id:" + id));
    }

    /**
     * Добавление в друзья
     * @param id       Идентификатор пользователя
     * @param friendId Идентификатор друга
     * @return CreatedUserDto пользователя
     */
    @PutMapping(path = "/{id}/friends/{friendId}")
    public ResponseEntity<CreatedUserDto> addAsFriend(@PathVariable long id,
                                                      @PathVariable long friendId) {
        if (id <= 0 || friendId <= 0) {
            throw new NotFoundException("Id пользователей должны быть положительными числами");
        }
        User currentUser = userService.addAsFriend(id, friendId);
        log.debug("Пользователь id={} добавил в друзья пользователя id={}", id, friendId);
        userService.addEvent(id, friendId, EventType.FRIEND, Operation.ADD);
        return ResponseEntity.ok(conversionService.convert(currentUser, CreatedUserDto.class));
    }

    /**
     * Удаление из друзей
     * @param id       Идентификатор пользователя
     * @param friendId Идентификатор друга
     * @return CreatedUserDto пользователя
     */
    @DeleteMapping(path = "/{id}/friends/{friendId}")
    public ResponseEntity<CreatedUserDto> removeFromFriends(@PathVariable long id,
                                                            @PathVariable long friendId) {
        if (id <= 0 || friendId <= 0) {
            throw new NotFoundException("Id пользователей должны быть положительными числами");
        }
        User currentUser = userService.removeFromFriends(id, friendId);
        log.debug("Пользователь id={} удалил из друзей пользователя id={}", id, friendId);
        userService.addEvent(id, friendId, EventType.FRIEND, Operation.REMOVE);
        return ResponseEntity.ok(conversionService.convert(currentUser, CreatedUserDto.class));
    }

    /**
     * Возвращает список друзей пользователя
     * @param id Идентификатор пользователя
     * @return список CreatedUserDto
     */
    @GetMapping(path = "/{id}/friends")
    public ResponseEntity<List<CreatedUserDto>> getUserFriends(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Некорректные параметры URL");
        }
        List<CreatedUserDto> friends = userService.getUserFriends(id)
                .stream()
                .map(user -> conversionService.convert(user, CreatedUserDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(friends);
    }

    /**
     * Возвращает список друзей, общих с другим пользователем.
     * @param id      Идентификатор пользователя
     * @param otherId Идентификатор другого пользователя
     * @return список CreatedUserDto
     */
    @GetMapping(path = "/{id}/friends/common/{otherId}")
    public ResponseEntity<List<CreatedUserDto>> getCommonFriends(@PathVariable long id,
                                                                 @PathVariable long otherId) {
        if (id <= 0 || otherId <= 0) {
            throw new NotFoundException("Id пользователей должны быть положительными числами");
        }
        List<CreatedUserDto> friends = userService.getCommonFriends(id, otherId)
                .stream()
                .map(user -> conversionService.convert(user, CreatedUserDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(friends);
    }

    /**
     * Возвращает рекомендации по фильмам для просмотра.
     * @param id id пользователя
     * @return List<CreatedFilmDto>
     */
    @GetMapping(path = "/{id}/recommendations")
    public ResponseEntity<List<CreatedFilmDto>> getRecommendations(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Id пользователей должны быть положительными числами");
        }

        List<CreatedFilmDto> films = recommendationService.getRecommendations(id)
                .stream()
                .map(film -> conversionService.convert(film, CreatedFilmDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(films);
    }

    /**
     * Возвращает ленту событий пользователя.
     * @param id id пользователя
     * @return List<CreatedEventDto>
     */
    @GetMapping(path = "/{id}/feed")
    public ResponseEntity<List<CreatedEventDto>> getFeedByUserId(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Id пользователей должны быть положительными числами");
        }
        List<CreatedEventDto> feed = userService.getFeedByUserId(id)
                .stream()
                .map(event -> conversionService.convert(event, CreatedEventDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(feed);
    }

    /**
     * Удаление пользователя
     * @param id id пользователя
     * @return CreatedUserDto
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<CreatedUserDto> deleteUserById(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Id пользователей должны быть положительными числами");
        }
        User deletedUser = userService.deleteUserById(id);
        log.debug("Пользователь id={} удален", id);
        return ResponseEntity.ok(conversionService.convert(deletedUser, CreatedUserDto.class));
    }
}
