package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmServiceImplTest {
    @Mock
    private FilmStorage filmStorage;

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private FilmServiceImpl filmService;

    @Test
    void getFilms_ifNotFound_thenReturnEmptyList() {
        when(filmStorage.getAllFilms()).thenReturn(new ArrayList<>());

        List<Film> films = filmService.getFilms();

        verify(filmStorage).getAllFilms();
        assertNotNull(films);
        assertTrue(films.isEmpty());
    }

    @Test
    void getFilms_ifFounded_thenReturnFounded() {
        var expected = Film.builder().id(1L).name("name").build();
        when(filmStorage.getAllFilms()).thenReturn(List.of(expected));

        var actual = filmService.getFilms();

        verify(filmStorage).getAllFilms();
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertSame(expected, actual.get(0));
    }

    @Test
    void createFilm_ifCreate_thenReturnCreated() {
        var expected = Film.builder().id(1L).name("name").build();
        when(filmStorage.createFilm(expected)).thenReturn(expected);

        var actual = filmService.createFilm(expected);

        verify(filmStorage).createFilm(expected);
        assertSame(expected, actual);
    }

    @Test
    void updateFilm_ifNotFound_thenReturnEmptyOptional() {
        var expected = Film.builder().id(1L).name("name").build();
        when(filmStorage.updateFilm(expected)).thenReturn(Optional.empty());

        var actual = filmService.updateFilm(expected);

        verify(filmStorage).updateFilm(expected);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void updateFilm_ifFounded_thenReturnUpdatedOptional() {
        var expected = Film.builder().id(1L).name("name").build();
        when(filmStorage.updateFilm(expected)).thenReturn(Optional.of(expected));

        var actual = filmService.updateFilm(expected);

        verify(filmStorage).updateFilm(expected);
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertSame(expected, actual.get());
    }

    @Test
    void getFilmById_ifNotFound_thenReturnEmptyOptional() {
        when(filmStorage.getFilmById(1L)).thenReturn(Optional.empty());

        var actual = filmService.getFilmById(1L);

        verify(filmStorage).getFilmById(1L);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void getFilmById_ifFounded_thenReturnFoundedOptional() {
        var expected = Film.builder().id(1L).name("name").build();
        when(filmStorage.getFilmById(1L)).thenReturn(Optional.of(expected));

        var actual = filmService.getFilmById(1L);

        verify(filmStorage).getFilmById(1L);
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertSame(expected, actual.get());
    }

    @Test
    void likeFilm_ifNotFoundFilm_thenThrowNotFoundException() {
        final long filmId = 1L;
        final long userId = 2L;
        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.likeFilm(filmId, userId));
        verify(filmStorage).getFilmById(filmId);
    }

    @Test
    void likeFilm_ifNotFoundUser_thenThrowNotFoundException() {
        final long filmId = 1L;
        final long userId = 2L;
        final Film film = Film.builder().id(filmId).name("name").build();
        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(userStorage.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.likeFilm(filmId, userId));
        verify(userStorage).getUserById(userId);
    }

    @Test
    void likeFilm_ifFilmAndUserFounded_thenUpdateFilmWithUsersLike() {
        final long filmId = 1L;
        final long userId = 2L;
        final Film film = Film.builder().id(filmId).name("filmname").build();
        final Film expected = Film.builder().id(filmId).name("filmname").likes(Set.of(userId)).build();
        final User user = User.builder().id(userId).name("username").build();
        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(userStorage.getUserById(userId)).thenReturn(Optional.of(user));
        when(filmStorage.updateFilm(any(Film.class))).thenReturn(Optional.of(expected));
        ArgumentCaptor<Film> filmCaptor = ArgumentCaptor.forClass(Film.class);

        Film result = filmService.likeFilm(filmId, userId);


        verify(filmStorage).getFilmById(filmId);
        verify(userStorage).getUserById(userId);
        verify(filmStorage).updateFilm(filmCaptor.capture());
        assertNotNull(result);
        assertSame(expected, result);
        assertEquals(expected, filmCaptor.getValue());
    }

    @Test
    void unlikeFilm_ifNotFoundFilm_thenThrowNotFoundException() {
        final long filmId = 1L;
        final long userId = 2L;
        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.unlikeFilm(filmId, userId));

        verify(filmStorage).getFilmById(filmId);
    }

    @Test
    void unlikeFilm_ifNotFoundUser_thenThrowNotFoundException() {
        final long filmId = 1L;
        final long userId = 2L;
        final Film film = Film.builder().id(filmId).name("name").build();
        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(userStorage.getUserById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> filmService.unlikeFilm(filmId, userId));

        verify(userStorage).getUserById(userId);
    }

    @Test
    void unlikeFilm_ifFilmAndUserFounded_thenUpdateFilmWithoutUsersLike() {
        final long filmId = 1L;
        final long userId = 2L;
        final Film film = Film.builder().id(filmId).name("filmname").build();
        film.addLike(userId);
        final Film expected = Film.builder().id(filmId).name("filmname").build();
        final User user = User.builder().id(userId).name("username").build();
        when(filmStorage.getFilmById(filmId)).thenReturn(Optional.of(film));
        when(userStorage.getUserById(userId)).thenReturn(Optional.of(user));
        when(filmStorage.updateFilm(any(Film.class))).thenReturn(Optional.of(expected));
        ArgumentCaptor<Film> filmCaptor = ArgumentCaptor.forClass(Film.class);

        Film result = filmService.unlikeFilm(filmId, userId);


        verify(filmStorage).getFilmById(filmId);
        verify(userStorage).getUserById(userId);
        verify(filmStorage).updateFilm(filmCaptor.capture());
        assertNotNull(result);
        assertSame(expected, result);
        assertEquals(expected, filmCaptor.getValue());
    }

    @Test
    void getMostPopularFilms_ifNotFound_thenReturnEmptyList() {
        when(filmStorage.getMostPopularFilms(10)).thenReturn(new ArrayList<>());

        List<Film> actual = filmService.getMostPopularFilms(10);

        verify(filmStorage).getMostPopularFilms(10);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void getMostPopularFilms_ifFounded_thenReturnFoundedList() {
        List<Film> expected = List.of(
                Film.builder().id(1L).build(),
                Film.builder().id(2L).build()
        );
        when(filmStorage.getMostPopularFilms(10)).thenReturn(expected);

        List<Film> actual = filmService.getMostPopularFilms(10);

        verify(filmStorage).getMostPopularFilms(10);
        assertSame(expected, actual);
    }
}