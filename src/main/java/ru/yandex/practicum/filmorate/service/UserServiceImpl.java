package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public List<User> getUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    @Override
    public Optional<User> updateUser(User userUpdates) {
        return userStorage.updateUser(userUpdates);
    }

    @Override
    public Optional<User> getUserById(long id) {
        return userStorage.getUserById(id);
    }

    @Override
    public User addAsFriend(long id, long friendId) throws NotFoundException, IllegalStateException {
        final User user = userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + id));
        final User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + friendId));
        user.addFriend(friendId);
        friend.addFriend(id);
        return userStorage.updateUser(friend)
                .flatMap(f -> userStorage.updateUser(user))
                .orElseThrow(() -> new IllegalStateException("Невозможно добавить в друзья"));
    }

    @Override
    public User removeFromFriends(long id, long friendId) throws NotFoundException, IllegalStateException {
        final User user = userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + id));
        final User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Не найден друг id=" + friendId));
        if(!user.removeFriend(friendId)) {
            throw new NotFoundException("У пользователя нет друга с id=" + friendId);
        }
        friend.removeFriend(id);
        return userStorage.updateUser(friend)
                .flatMap(f -> userStorage.updateUser(user))
                .orElseThrow(() -> new IllegalStateException("Невозможно удалить из друзей"));
    }

    @Override
    public List<User> getUserFriends(long id) {
        final User user = userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + id));
        return userStorage.getUsersById(Collections.unmodifiableSet(user.getFriends()));
    }

    @Override
    public List<User> getCommonFriends(long id, long otherId) throws NotFoundException {
        final User currentUser = userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + id));
        final User otherUser = userStorage.getUserById(otherId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + id));

        HashSet<User> commonFriends = new HashSet<>(
                userStorage.getUsersById(Collections.unmodifiableSet(currentUser.getFriends())));
        HashSet<User> otherFriends = new HashSet<>(
                userStorage.getUsersById(Collections.unmodifiableSet(otherUser.getFriends())));
        commonFriends.retainAll(otherFriends);
        return List.copyOf(commonFriends);
    }
}
