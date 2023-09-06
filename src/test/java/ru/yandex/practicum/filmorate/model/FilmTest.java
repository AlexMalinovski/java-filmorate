package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

class FilmTest {

    @Test
    void copyOf() {
        Film film = new Film(1L, "name", "description", LocalDate.of(1990, 1, 1),
                Duration.ofMinutes(120), new HashSet<>(Arrays.asList(1L, 5L, 7L)));
        Film copy = film.copyOf();
        assertEquals(film, copy);
        assertNotSame(film, copy);
        assertNotSame(film.getLikes(), copy.getLikes());
    }

    @Test
    void addLike() {
        Film film = Film.builder().build();
        assertEquals(0, film.getLikes().size());
        film.addLike(1L);
        assertEquals(1, film.getLikes().size());
    }

    @Test
    void removeLike() {
        Film film = Film.builder()
                .likes(new HashSet<>(Arrays.asList(1L, 5L, 7L)))
                .build();
        assertEquals(3, film.getLikes().size());
        film.removeLike(5L);
        assertEquals(2, film.getLikes().size());
    }

    @Test
    void getNumOfLikes() {
        Film film = Film.builder().build();
        assertEquals(0, film.getNumOfLikes());

        film = Film.builder()
                .likes(new HashSet<>(Arrays.asList(1L, 5L, 7L)))
                .build();
        assertEquals(3, film.getNumOfLikes());
    }
}