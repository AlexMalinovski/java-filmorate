package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmLike;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.FilmStorage;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleRecommendationServiceTest {

    @Mock
    private FilmStorage filmStorage;

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    SimpleRecommendationService recommendationService;

    @Test
    void getRecommendations_ifUserNotFound_thenThrowNotFoundException() {
        when(userStorage.getUserById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> recommendationService.getRecommendations(1L));
    }

    @Test
    void getRecommendations() {
        Film expected = Film.builder().id(2L).build();
        when(userStorage.getUserById(anyLong())).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(filmStorage.getAllFilmLikes()).thenReturn(List.of(
                new FilmLike(1L, 1L),
                new FilmLike(1L, 2L),
                new FilmLike(2L, 2L)));
        when(filmStorage.getFilmsByIds(Set.of(2L))).thenReturn(List.of(expected));

        var actual = recommendationService.getRecommendations(1L);

        verify(userStorage).getUserById(1L);
        verify(filmStorage).getAllFilmLikes();
        verify(filmStorage).getFilmsByIds(Set.of(2L));
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }
}