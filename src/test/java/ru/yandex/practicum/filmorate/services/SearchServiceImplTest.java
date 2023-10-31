package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmSort;
import ru.yandex.practicum.filmorate.storages.DirectorStorage;
import ru.yandex.practicum.filmorate.storages.FilmStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {
    @Mock
    private FilmStorage filmStorage;

    @Mock
    private DirectorStorage directorStorage;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void getFilmsByTitle_whenNotFound_thenReturnEmptyList() {
        when(filmStorage.getFilmsByTitle("some title")).thenReturn(new ArrayList<>());

        List<Film> actual = searchService.getFilmsByTitle("some title");

        verify(filmStorage).getFilmsByTitle("some title");
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void getFilmsByTitle_whenCalled_thenReturnListOfFilms() {
        Film film = Film.builder()
                .id(1L)
                .name("needName")
                .build();
        Film secondFilm = Film.builder()
                .id(2L)
                .name("need")
                .build();
        when(filmStorage.getFilmsByTitle("need")).thenReturn(List.of(film, secondFilm));

        List<Film> actual = searchService.getFilmsByTitle("need");

        verify(filmStorage).getFilmsByTitle("need");
        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(1L, actual.get(0).getId());
        assertEquals(2L, actual.get(1).getId());
    }

    @Test
    void getFilmsByDirectorsName_whenNotFound_thenReturnEmptyList() {
        Director director = Director.builder().id(1L).name("newDirector").build();
        when(directorStorage.getDirectorsByName("newdirect")).thenReturn(new ArrayList<>());

        List<Film> actual = searchService.getFilmsByDirectorsName("newDirect");

        verify(directorStorage).getDirectorsByName("newdirect");
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void getFilmsByDirectorsName_whenCalled_thenReturnListOfFilms() {
        Director director = Director.builder().id(1L).name("newDirector").build();
        Film film = Film.builder()
                .id(1L)
                .name("Name")
                .directors(Set.of(director))
                .build();
        Film secondFilm = Film.builder()
                .id(2L)
                .name("need")
                .directors(Set.of(director))
                .build();
        when(directorStorage.getDirectorsByName("new")).thenReturn(List.of(director));
        when(filmStorage.getFilmsByDirector(director.getId(), FilmSort.LIKES)).thenReturn(List.of(film, secondFilm));

        List<Film> actual = searchService.getFilmsByDirectorsName("new");

        verify(directorStorage).getDirectorsByName("new");
        verify(filmStorage).getFilmsByDirector(director.getId(), FilmSort.LIKES);
        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertEquals(2L, actual.get(0).getId());
        assertEquals(1L, actual.get(1).getId());
    }
}