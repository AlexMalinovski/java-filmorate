package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.models.Film;

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

class InMemoryFilmStorageTest {

    @Test
    void getAllFilms_ifEmptyStorage_thenReturnEmptyList() {
        InMemoryFilmStorage storage = new InMemoryFilmStorage();
        List<Film> films = storage.getAllFilms();
        assertNotNull(films);
        assertEquals(0, films.size());
    }

    @Test
    void getAllFilms_ifNotEmptyStorage_thenReturnAllRecords() {
        InMemoryFilmStorage storage = new InMemoryFilmStorage(Map.of(1L, Film.builder().id(1L).build()),
                new AtomicLong(2L));
        List<Film> films = storage.getAllFilms();
        assertNotNull(films);
        assertEquals(1, films.size());
    }

    @Test
    void createFilm_isSetCurrentIdAndIgnoreTransferredId() {
        InMemoryFilmStorage storage = new InMemoryFilmStorage();
        long currentId = storage.getCurrentId().get();
        Film film = Film.builder().id(100L).build();
        assertEquals(0, storage.getFilms().size());

        storage.createFilm(film);
        Film stored = storage.getFilms().get(currentId);

        assertEquals(1, storage.getFilms().size());
        assertNotNull(stored);
        assertEquals(currentId, stored.getId());
    }

    @Test
    void createFilm_isSaveCopyAndIncrementCurrentId() {
        InMemoryFilmStorage storage = new InMemoryFilmStorage();
        long currentId = storage.getCurrentId().get();
        Film film = Film.builder()
                .id(currentId)
                .name("name")
                .description("description")
                .build();

        storage.createFilm(film);
        Film stored = storage.getFilms().get(currentId);

        assertEquals(film, stored);
        assertNotSame(film, stored);
        assertEquals(++currentId, storage.getCurrentId().get());
    }

    @Test
    void createFilm_ifAdded_thenReturnCopy() {
        InMemoryFilmStorage storage = new InMemoryFilmStorage();
        Film film = Film.builder().build();
        long currentId = storage.getCurrentId().get();

        Film returned = storage.createFilm(film);
        Film stored = storage.getFilms().get(currentId);

        assertEquals(stored, returned);
        assertNotSame(stored, returned);
    }

    @Test
    void updateFilm_ifNotFound_thenReturnEmpty() {
        InMemoryFilmStorage storage = new InMemoryFilmStorage();
        Film film = Film.builder().id(100L).build();

        Optional<Film> updated = storage.updateFilm(film);

        assertNotNull(updated);
        assertTrue(updated.isEmpty());
    }

    @Test
    void updateFilm_ifFounded_thenReturnCopy() {
        Map<Long, Film> films = new HashMap<>();
        films.put(1L, Film.builder().id(1L).build());
        InMemoryFilmStorage storage = new InMemoryFilmStorage(films, new AtomicLong(2L));
        Film film = Film.builder().id(1L).name("newName").build();

        Optional<Film> updated = storage.updateFilm(film);

        assertNotNull(updated);
        assertFalse(updated.isEmpty());
        assertEquals(film, updated.get());
        assertNotSame(film, updated.get());
    }

    @Test
    void getFilmById_ifNotFound_thenReturnEmpty() {
        InMemoryFilmStorage storage = new InMemoryFilmStorage();

        Optional<Film> founded = storage.getFilmById(100L);

        assertNotNull(founded);
        assertTrue(founded.isEmpty());
    }

    @Test
    void getFilmById_ifFounded_thenReturnCopy() {
        InMemoryFilmStorage storage = new InMemoryFilmStorage(Map.of(1L, Film.builder().id(1L).name("name").build()),
                new AtomicLong(2L));
        Film stored = storage.getFilms().get(1L);

        Optional<Film> founded = storage.getFilmById(1L);

        assertNotNull(founded);
        assertFalse(founded.isEmpty());
        assertEquals(stored, founded.get());
        assertNotSame(stored, founded.get());
    }

    @Test
    void getMostPopularFilms_ifEmptyStorage_thenReturnEmptyList() {
        InMemoryFilmStorage storage = new InMemoryFilmStorage();
        List<Film> films = storage.getMostPopularFilms(10);
        assertNotNull(films);
        assertEquals(0, films.size());
    }

    @Test
    void getMostPopularFilms_isReturnSortedDescByNumLikes() {
        Film film1 = Film.builder().id(1L).likes(Set.of(1L, 2L, 3L)).build();
        Film film2 = Film.builder().id(2L).likes(Set.of(1L, 2L)).build();
        Film film3 = Film.builder().id(3L).build();
        InMemoryFilmStorage storage = new InMemoryFilmStorage();
        storage.getFilms().put(film2.getId(), film2);
        storage.getFilms().put(film1.getId(), film1);
        storage.getFilms().put(film3.getId(), film3);

        List<Film> mostPopularFilms = storage.getMostPopularFilms(2);

        assertEquals(2, mostPopularFilms.size());
        assertEquals(film1, mostPopularFilms.get(0));
        assertEquals(film2, mostPopularFilms.get(1));
    }
}