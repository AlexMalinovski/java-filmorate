package ru.yandex.practicum.filmorate.services;

import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.Operation;
import ru.yandex.practicum.filmorate.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    @NonNull
    List<User> getUsers();

    @NonNull
    User createUser(@NonNull User user);

    @NonNull
    Optional<User> updateUser(@NonNull User userUpdates);

    @NonNull
    Optional<User> getUserById(long id);

    @NonNull
    User addAsFriend(long id, long friendId) throws NotFoundException, IllegalStateException;

    @NonNull
    User removeFromFriends(long id, long friendId) throws NotFoundException, IllegalStateException;

    @NonNull
    List<User> getUserFriends(long id);

    @NonNull
    List<User> getCommonFriends(long id, long otherId) throws NotFoundException;

    @NonNull
    List<Event> getFeedByUserId(long id) throws NotFoundException;

    @NonNull
    Event addEvent(long userId, long entityId, EventType et, Operation op) throws NotFoundException;

    @NonNull
    User deleteUserById(long id) throws NotFoundException, IllegalStateException;

}
