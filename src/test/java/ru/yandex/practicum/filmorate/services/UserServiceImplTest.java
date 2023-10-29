package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.FeedStorage;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserStorage userStorage;

    @Mock
    private FeedStorage feedStorage;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUsers_ifNotFound_thenReturnEmptyList() {
        when(userStorage.getAllUsers()).thenReturn(new ArrayList<>());

        var actual = userService.getUsers();

        verify(userStorage).getAllUsers();
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void getUsers_ifFounded_thenReturnFounded() {
        var expected = User.builder().id(1L).name("name").build();
        when(userStorage.getAllUsers()).thenReturn(List.of(expected));

        var actual = userService.getUsers();

        verify(userStorage).getAllUsers();
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertSame(expected, actual.get(0));
    }

    @Test
    void createUser_ifCreate_thenReturnCreated() {
        var expected = User.builder().id(1L).name("name").build();
        when(userStorage.createUser(expected)).thenReturn(expected);

        var actual = userService.createUser(expected);

        verify(userStorage).createUser(expected);
        assertSame(expected, actual);
    }

    @Test
    void updateUser_ifNotFound_thenReturnEmptyOptional() {
        var expected = User.builder().id(1L).name("name").build();
        when(userStorage.updateUser(expected)).thenReturn(Optional.empty());

        var actual = userService.updateUser(expected);

        verify(userStorage).updateUser(expected);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void updateUser_ifFounded_thenReturnUpdatedOptional() {
        var expected = User.builder().id(1L).name("name").build();
        when(userStorage.updateUser(expected)).thenReturn(Optional.of(expected));

        var actual = userService.updateUser(expected);

        verify(userStorage).updateUser(expected);
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertSame(expected, actual.get());
    }

    @Test
    void getUserById_ifNotFound_thenReturnEmptyOptional() {
        when(userStorage.getUserById(1L)).thenReturn(Optional.empty());

        var actual = userService.getUserById(1L);

        verify(userStorage).getUserById(1L);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void getUserById_ifFounded_thenReturnFoundedOptional() {
        var expected = User.builder().id(1L).name("name").build();
        when(userStorage.getUserById(1L)).thenReturn(Optional.of(expected));

        var actual = userService.getUserById(1L);

        verify(userStorage).getUserById(1L);
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertSame(expected, actual.get());
    }

    @Test
    void addAsFriend_ifNotFoundUser_thenThrowNotFoundException() {
        final long id = 1L;
        final long friendId = 2L;
        when(userStorage.getUserById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.addAsFriend(id, friendId));

        verify(userStorage).getUserById(id);
    }

    @Test
    void addAsFriend_ifNotFoundFriend_thenThrowNotFoundException() {
        final long id = 1L;
        final long friendId = 2L;
        User user = User.builder().id(id).name("user").build();
        when(userStorage.getUserById(id)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(friendId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.addAsFriend(id, friendId));

        verify(userStorage).getUserById(friendId);
    }

    @Test
    void addAsFriend_ifUserAndFriendFounded_thenAddToFriendsMutually() {
        final long id = 1L;
        final long friendId = 2L;
        User user = User.builder().id(id).name("user").build();
        User expectedUser = User.builder().id(id).name("user").friends(Set.of(friendId)).build();
        User friend = User.builder().id(friendId).name("friend").build();
        when(userStorage.getUserById(id)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(friendId)).thenReturn(Optional.of(friend));

        User actualUser = userService.addAsFriend(id, friendId);

        verify(userStorage).createFriend(id, friendId);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void removeFromFriends_ifNotFoundUser_thenThrowNotFoundException() {
        final long id = 1L;
        final long friendId = 2L;
        when(userStorage.getUserById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.removeFromFriends(id, friendId));

        verify(userStorage).getUserById(id);
    }

    @Test
    void removeFromFriends_ifNotFoundFriend_thenThrowNotFoundException() {
        final long id = 1L;
        final long friendId = 2L;
        User user = User.builder().id(id).name("user").build();
        when(userStorage.getUserById(id)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(friendId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.removeFromFriends(id, friendId));

        verify(userStorage).getUserById(friendId);
    }

    @Test
    void removeFromFriends_ifFriendIsNotInUsersFriendsList_thenThrowNotFoundException() {
        final long id = 1L;
        final long friendId = 2L;
        User user = User.builder().id(id).name("user").build();
        User friend = User.builder().id(friendId).name("friend").build();
        when(userStorage.getUserById(id)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(friendId)).thenReturn(Optional.of(friend));

        assertThrows(NotFoundException.class, () -> userService.removeFromFriends(id, friendId));
    }

    @Test
    void removeFromFriends_ifUserAndFriendFounded_thenRemoveFromFriendsMutually() {
        final long id = 1L;
        final long friendId = 2L;
        User user = User.builder().id(id).name("user").friends(new HashSet<>(List.of(friendId))).build();
        User friend = User.builder().id(friendId).name("friend").friends(new HashSet<>(List.of(id))).build();
        User expectedUser = User.builder().id(id).name("user").build();
        when(userStorage.getUserById(id)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(friendId)).thenReturn(Optional.of(friend));

        User actualUser = userService.removeFromFriends(id, friendId);

        verify(userStorage).removeFriend(id, friendId);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserFriends_ifNotFoundUser_thenThrowNotFoundException() {
        final long id = 1L;
        when(userStorage.getUserById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserFriends(id));

        verify(userStorage).getUserById(id);
    }

    @Test
    void getUserFriends_ifUserFounded_thenReturnUserFriends() {
        final long id = 1L;
        final long friendId = 2L;
        final Set<Long> friendSet = Set.of(friendId);
        final User user = User.builder().id(id).friends(friendSet).build();
        final User friend = User.builder().id(friendId).build();
        when(userStorage.getUserById(id)).thenReturn(Optional.of(user));
        when(userStorage.getUserFriends(id)).thenReturn(List.of(friend));

        List<User> actual = userService.getUserFriends(id);

        verify(userStorage).getUserFriends(id);
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertSame(friend, actual.get(0));
    }

    @Test
    void getCommonFriends_ifNotFoundUser_thenThrowNotFoundException() {
        final long id = 1L;
        final long otherId = 1L;
        when(userStorage.getUserById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getCommonFriends(id, otherId));

        verify(userStorage).getUserById(id);
    }

    @Test
    void getCommonFriends_ifNotFoundOtherUser_thenThrowNotFoundException() {
        final long id = 1L;
        final long otherId = 2L;
        User user = User.builder().id(id).name("user").build();
        when(userStorage.getUserById(id)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(otherId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getCommonFriends(id, otherId));

        verify(userStorage).getUserById(otherId);
    }

    @Test
    void getCommonFriends_ifUsersFounded_thenReturnCommonFriends() {
        final long id = 1L;
        final long otherId = 2L;
        final Set<Long> usersFriends = Set.of(4L, 5L);
        final Set<Long> otherFriends = Set.of(5L, 6L);
        final User user = User.builder().id(id).friends(usersFriends).build();
        final User other = User.builder().id(otherId).friends(otherFriends).build();
        final User friend4 = User.builder().id(4L).build();
        final User friend5 = User.builder().id(5L).build();
        final User friend6 = User.builder().id(6L).build();
        when(userStorage.getUserById(id)).thenReturn(Optional.of(user));
        when(userStorage.getUserById(otherId)).thenReturn(Optional.of(other));
        when(userStorage.getUserFriends(id)).thenReturn(List.of(friend4, friend5));
        when(userStorage.getUserFriends(otherId)).thenReturn(List.of(friend5, friend6));

        List<User> actual = userService.getCommonFriends(id, otherId);

        verify(userStorage).getUserFriends(id);
        verify(userStorage).getUserFriends(otherId);
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertSame(friend5, actual.get(0));
    }

    @Test
    void getFeedByUserId_ifFounded_ThenReturnFilledList() {
        var user = User.builder().id(1L).login("nameOne").name("nameOne").build();
        var expected = Event.builder().eventId(1L).userId(1L).build();
        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(feedStorage.getFeedByUser(any())).thenReturn(List.of(expected));

        var actual = userService.getFeedByUserId(1L);
        verify(feedStorage).getFeedByUser(user);
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertSame(expected, actual.get(0));

    }

    @Test
    void getFeedByUserId_ifNotFound_ThenReturnEmptyList() {
        var user = User.builder().id(1L).login("nameOne").name("nameOne").build();
        when(userStorage.getUserById(1L)).thenReturn(Optional.of(user));
        when(feedStorage.getFeedByUser(any())).thenReturn(new ArrayList<>());

        var actual = userService.getFeedByUserId(1L);

        verify(feedStorage).getFeedByUser(user);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());

    }
}