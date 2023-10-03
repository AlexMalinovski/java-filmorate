package ru.yandex.practicum.filmorate.storages;

import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.models.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    List<User> getAllUsers();

    User createUser(@NonNull User user);

    Optional<User> updateUser(@NonNull User userUpdates);

    Optional<User> getUserById(long id);

    List<User> getUsersById(@NonNull Set<Long> ids);

    void createFriend(long userId, long friendId);

    void removeFriend(long userId, long friendId);

    List<User> getUserFriends(long id);
}
