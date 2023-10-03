package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DbUserStorageTest {

    @Autowired
    @Qualifier("dbUserStorage")
    UserStorage userStorage;

    @Test
    @Sql({"/test-data.sql"})
    void getAllUsers() {
        var actual = userStorage.getAllUsers();

        assertEquals(3, actual.size());
    }

    @Test
    @Sql({"/data-clear.sql"})
    void createUser() {
        User user = User.builder()
                .name("new_user")
                .email("e@mail.ru")
                .login("login")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();

        var actual = Optional.of(userStorage.createUser(user));

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("name", "new_user");
                    assertThat(obj).hasFieldOrPropertyWithValue("email", "e@mail.ru");
                    assertThat(obj).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1980, 1, 1));
                    assertThat(obj).hasFieldOrPropertyWithValue("login", "login");
                });
    }

    @Test
    @Sql({"/test-data.sql"})
    void updateUser() {
        User user = User.builder()
                .id(1L)
                .name("updated_user")
                .email("e@mail.ru")
                .login("login")
                .birthday(LocalDate.of(1980, 1, 1))
                .build();

        var actual = userStorage.updateUser(user);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("name", "updated_user");
                    assertThat(obj).hasFieldOrPropertyWithValue("email", "e@mail.ru");
                    assertThat(obj).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1980, 1, 1));
                    assertThat(obj).hasFieldOrPropertyWithValue("login", "login");
                });
    }

    @Test
    @Sql({"/test-data.sql"})
    void getUserById() {
        var actual = userStorage.getUserById(1L);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("name", "user1");
                    assertThat(obj).hasFieldOrPropertyWithValue("email", "user1@mail.ru");
                    assertThat(obj).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1950, 1, 1));
                    assertThat(obj).hasFieldOrPropertyWithValue("login", "user1");
                    assertThat(obj).hasFieldOrPropertyWithValue("friends", Set.of(3L, 2L));
                });
    }

    @Test
    @Sql({"/test-data.sql"})
    void getUsersById() {
        var actual = userStorage.getUsersById(Set.of(1L));

        assertEquals(1, actual.size());
        assertEquals(1L, actual.get(0).getId());
    }

    @Test
    @Sql({"/test-data.sql"})
    void createFriend() {
        userStorage.createFriend(3L, 1L);
        var actual = userStorage.getUserById(3L);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 3L);
                    assertThat(obj).hasFieldOrPropertyWithValue("friends", Set.of(1L));
                });
    }

    @Test
    @Sql({"/test-data.sql"})
    void removeFriend() {
        userStorage.removeFriend(1L, 2L);
        var actual = userStorage.getUserById(1L);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("friends", Set.of(3L));
                });
    }

    @Test
    @Sql({"/test-data.sql"})
    void getUserFriends() {
        List<User> actual = userStorage.getUserFriends(1L)
                .stream()
                .sorted(Comparator.comparingLong(User::getId))
                .collect(Collectors.toList());

        assertEquals(2, actual.size());
        assertEquals(2L, actual.get(0).getId());
        assertEquals(3L, actual.get(1).getId());
    }
}