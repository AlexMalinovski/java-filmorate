package ru.yandex.practicum.filmorate.storages;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.User;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users;
    private final AtomicLong currentId;

    public InMemoryUserStorage() {
        this.users = new ConcurrentHashMap<>();
        this.currentId = new AtomicLong(1L);
    }

    @Override
    public List<User> getAllUsers() {
        return users.values()
                .stream()
                .map(User::copyOf)
                .collect(Collectors.toList());
    }

    @Override
    public User createUser(User user) {
        final User createdUser = user.copyOf();
        createdUser.setId(currentId.getAndIncrement());
        users.put(createdUser.getId(), createdUser);
        return createdUser.copyOf();
    }

    @Override
    public Optional<User> updateUser(User userUpdates) {
        return Optional.ofNullable(users.computeIfPresent(userUpdates.getId(), (k, v) -> userUpdates.copyOf()))
                .map(User::copyOf);
    }

    @Override
    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id))
                .map(User::copyOf);
    }

    @Override
    public List<User> getUsersById(Set<Long> ids) {
        return users.values()
                .stream()
                .filter(u -> ids.contains(u.getId()))
                .map(User::copyOf)
                .collect(Collectors.toList());
    }

    @Override
    public void createFriend(long userId, long friendId) {
        users.computeIfPresent(userId, (id, user) -> {
            user.addFriend(friendId);
            return user;
        });
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        users.computeIfPresent(userId, (id, user) -> {
            user.removeFriend(friendId);
            return user;
        });
    }

    @Override
    public List<User> getUserFriends(long id) {
        throw new IllegalStateException("Not implemented!");
    }
}
