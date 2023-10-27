package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.DirectorStorage;
import ru.yandex.practicum.filmorate.storages.FilmStorage;
import ru.yandex.practicum.filmorate.storages.GenreStorage;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilmServiceImplTest {
    @Mock
    private FilmStorage filmStorage;

    @Mock
    private GenreStorage genreStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private DirectorStorage directorStorage;

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
        when(filmStorage.getFilmById(expected.getId())).thenReturn(Optional.of(expected));

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

        Film result = filmService.likeFilm(filmId, userId);

        verify(filmStorage).getFilmById(filmId);
        verify(userStorage).getUserById(userId);
        verify(filmStorage).createFilmLike(filmId, userId);
        assertNotNull(result);
        assertEquals(expected, result);
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

        Film result = filmService.unlikeFilm(filmId, userId);

        verify(filmStorage).getFilmById(filmId);
        verify(userStorage).getUserById(userId);
        verify(filmStorage).removeFilmLike(filmId, userId);
        assertNotNull(result);
        assertEquals(expected, result);
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

    @Test
    void getGenreById_ifNotFound_thenReturnEmptyOptional() {
        when(genreStorage.getGenreById(1L)).thenReturn(Optional.empty());

        var actual = filmService.getGenreById(1L);

        verify(genreStorage).getGenreById(1L);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void getGenreById_ifFounded_thenReturnFoundedOptional() {
        var expected = Genre.builder().id(1L).name("name").build();
        when(genreStorage.getGenreById(1L)).thenReturn(Optional.of(expected));

        var actual = filmService.getGenreById(1L);

        verify(genreStorage).getGenreById(1L);
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertSame(expected, actual.get());
    }

    @Test
    void getGenres_ifNotFound_thenReturnEmptyList() {
        when(genreStorage.getAllGenres()).thenReturn(new ArrayList<>());

        var actual = filmService.getGenres();

        verify(genreStorage).getAllGenres();
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void getGenres_ifFounded_thenReturnFounded() {
        var expected = Genre.builder().id(1L).name("name").build();
        when(genreStorage.getAllGenres()).thenReturn(List.of(expected));

        var actual = filmService.getGenres();

        verify(genreStorage).getAllGenres();
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertSame(expected, actual.get(0));
    }
}