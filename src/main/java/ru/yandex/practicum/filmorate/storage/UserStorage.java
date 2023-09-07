package ru.yandex.practicum.filmorate.storage;

import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    @NonNull
    List<User> getAllUsers();

    @NonNull
    User createUser(@NonNull User user);

    @NonNull
    Optional<User> updateUser(@NonNull User userUpdates);

    @NonNull
    Optional<User> getUserById(long id);

    @NonNull
    List<User> getUsersById(@NonNull Set<Long> ids);
}
