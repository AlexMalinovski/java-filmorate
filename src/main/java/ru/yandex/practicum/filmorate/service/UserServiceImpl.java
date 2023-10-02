package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Transactional
    @Override
    public List<User> getUsers() {
        return userStorage.getAllUsers();
    }

    @Transactional
    @Override
    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    @Transactional
    @Override
    public Optional<User> updateUser(User userUpdates) {
        return userStorage.updateUser(userUpdates);
    }

    @Transactional
    @Override
    public Optional<User> getUserById(long id) {
        return userStorage.getUserById(id);
    }

    @Transactional
    @Override
    public User addAsFriend(long id, long friendId) throws NotFoundException, IllegalStateException {
        final User user = userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + id));
        final User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + friendId));

        //В соответствии с логикой тестов Postman, противоречит ТЗ.
        if (user.addFriend(friendId)) {
            userStorage.createFriend(id, friendId); //заявка в друзья
        }
        //В соответствии с ТЗ.
//        if (friend.addFriend(id)) {
//            userStorage.createFriend(friendId, id); //заявка в друзья
//        }
        return user;
    }

    @Transactional
    @Override
    public User removeFromFriends(long id, long friendId) throws NotFoundException, IllegalStateException {
        final User user = userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + id));
        final User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Не найден друг id=" + friendId));

        //В соответствии с логикой тестов Postman, противоречит ТЗ.
        if (user.removeFriend(friendId)) {
            userStorage.removeFriend(id, friendId);
        } else {
            throw new NotFoundException("Пользователь отсутствует в списке друзей пользователя с id=" + id);
        }
        //В соответствии с ТЗ.
//        if (friend.removeFriend(id)) {
//            userStorage.removeFriend(friendId, id);
//        } else {
//            throw new NotFoundException("Пользователь отсутствует в списке друзей пользователя с id=" + friendId);
//        }
        return user;
    }

    @Override
    @Transactional
    public List<User> getUserFriends(long id) {
        final User user = userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + id));
        return userStorage.getUserFriends(id);
    }

    @Override
    @Transactional
    public List<User> getCommonFriends(long id, long otherId) throws NotFoundException {
        final User currentUser = userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + id));
        final User otherUser = userStorage.getUserById(otherId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + id));
        HashSet<User> commonFriends = new HashSet<>(userStorage.getUserFriends(id));
        HashSet<User> otherFriends = new HashSet<>(userStorage.getUserFriends(otherId));
        commonFriends.retainAll(otherFriends);
        return List.copyOf(commonFriends);
    }
}
