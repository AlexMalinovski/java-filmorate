package ru.yandex.practicum.filmorate.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class UserTest {

    @Test
    void copyOf() {
        User user = new User(1L, "e@mail.ru", "login", "name",
                LocalDate.of(1990, 1,1), new HashSet<>(Arrays.asList(1L, 5L, 7L)));
        User copy = user.copyOf();
        assertEquals(user, copy);
        assertNotSame(user, copy);
        assertNotSame(user.getFriends(), copy.getFriends());
    }

    @Test
    void addFriend() {
        User user = User.builder().build();
        assertEquals(0, user.getFriends().size());
        user.addFriend(2L);
        assertEquals(1, user.getFriends().size());
    }

    @Test
    void removeFriend() {
        User user = User.builder()
                .friends(new HashSet<>(Arrays.asList(2L, 5L, 7L)))
                .build();
        assertEquals(3, user.getFriends().size());
        user.removeFriend(5L);
        assertEquals(2, user.getFriends().size());
    }

    @Test
    void getNumOfFriends() {
        User user = User.builder().build();
        assertEquals(0, user.getNumOfFriends());

        user = User.builder()
                .friends(new HashSet<>(Arrays.asList(2L, 5L, 7L)))
                .build();
        assertEquals(3, user.getNumOfFriends());
    }
}