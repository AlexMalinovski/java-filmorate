package ru.yandex.practicum.filmorate.service;

import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

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
}
