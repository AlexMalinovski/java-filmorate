package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.models.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryUserStorageTest {

    @Test
    void getAllUsers_ifEmptyStorage_thenReturnEmptyList() {
        InMemoryUserStorage storage = new InMemoryUserStorage();
        List<User> users = storage.getAllUsers();
        assertNotNull(users);
        assertEquals(0, users.size());
    }

    @Test
    void getAllUsers_ifNotEmptyStorage_thenReturnAllRecords() {
        InMemoryUserStorage storage = new InMemoryUserStorage(Map.of(1L, User.builder().id(1L).build()),
                new AtomicLong(2L));
        List<User> users = storage.getAllUsers();
        assertNotNull(users);
        assertEquals(1, users.size());
    }

    @Test
    void createUser_isSetCurrentIdAndIgnoreTransferredId() {
        InMemoryUserStorage storage = new InMemoryUserStorage();
        long currentId = storage.getCurrentId().get();
        User user = User.builder().id(100L).build();
        assertEquals(0, storage.getUsers().size());

        storage.createUser(user);
        User stored = storage.getUsers().get(currentId);

        assertEquals(1, storage.getUsers().size());
        assertNotNull(stored);
        assertEquals(currentId, stored.getId());
    }

    @Test
    void createUser_isSaveCopyAndIncrementCurrentId() {
        InMemoryUserStorage storage = new InMemoryUserStorage();
        long currentId = storage.getCurrentId().get();
        User user = User.builder()
                .id(currentId)
                .name("name")
                .email("e@mail.ru")
                .build();

        storage.createUser(user);
        User stored = storage.getUsers().get(currentId);

        assertEquals(user, stored);
        assertNotSame(user, stored);
        assertEquals(++currentId, storage.getCurrentId().get());
    }

    @Test
    void createUser_ifAdded_thenReturnCopy() {
        InMemoryUserStorage storage = new InMemoryUserStorage();
        User user = User.builder().build();
        long currentId = storage.getCurrentId().get();

        User returned = storage.createUser(user);
        User stored = storage.getUsers().get(currentId);

        assertEquals(stored, returned);
        assertNotSame(stored, returned);
    }

    @Test
    void updateUser_ifNotFound_thenReturnEmpty() {
        InMemoryUserStorage storage = new InMemoryUserStorage();
        User user = User.builder().id(100L).build();

        Optional<User> updated = storage.updateUser(user);

        assertNotNull(updated);
        assertTrue(updated.isEmpty());
    }

    @Test
    void updateUser_ifFounded_thenReturnCopy() {
        Map<Long, User> users = new HashMap<>();
        users.put(1L, User.builder().id(1L).build());
        InMemoryUserStorage storage = new InMemoryUserStorage(users, new AtomicLong(2L));
        User user = User.builder().id(1L).name("newName").build();

        Optional<User> updated = storage.updateUser(user);

        assertNotNull(updated);
        assertFalse(updated.isEmpty());
        assertEquals(user, updated.get());
        assertNotSame(user, updated.get());
    }

    @Test
    void getUserById_ifNotFound_thenReturnEmpty() {
        InMemoryUserStorage storage = new InMemoryUserStorage();

        Optional<User> founded = storage.getUserById(100L);

        assertNotNull(founded);
        assertTrue(founded.isEmpty());
    }

    @Test
    void getUserById_ifFounded_thenReturnCopy() {
        InMemoryUserStorage storage = new InMemoryUserStorage(Map.of(1L, User.builder().id(1L).name("name").build()),
                new AtomicLong(2L));
        User stored = storage.getUsers().get(1L);

        Optional<User> founded = storage.getUserById(1L);

        assertNotNull(founded);
        assertFalse(founded.isEmpty());
        assertEquals(stored, founded.get());
        assertNotSame(stored, founded.get());
    }

    @Test
    void getUsersById_ifNotFound_thenReturnEmptyList() {
        InMemoryUserStorage storage = new InMemoryUserStorage();

        List<User> founded = storage.getUsersById(Set.of(100L, 200L));

        assertNotNull(founded);
        assertTrue(founded.isEmpty());
    }

    @Test
    void getUsersById_ifFounded_thenReturnCopy() {
        User user1 = User.builder().id(1L).name("name1").build();
        User user2 = User.builder().id(2L).name("name2").build();
        InMemoryUserStorage storage = new InMemoryUserStorage();
        storage.getUsers().put(user2.getId(), user2);
        storage.getUsers().put(user1.getId(), user1);

        List<User> founded = storage.getUsersById(Set.of(1L));

        assertNotNull(founded);
        assertEquals(1, founded.size());
        assertEquals(user1, founded.get(0));
        assertNotSame(user1, founded.get(0));
    }
}