package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.models.FilmSort;
import ru.yandex.practicum.filmorate.models.Genre;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DbFilmStorageTest {

    @Autowired
    @Qualifier("dbFilmStorage")
    private FilmStorage filmStorage;

    @Test
    @Sql({"/test-data.sql"})
    void getAllFilms() {
        var actual = filmStorage.getAllFilms();

        assertEquals(3, actual.size());
    }

    @Test
    @Sql({"/data-clear.sql"})
    void createFilm() {
        Film film = Film.builder()
                .name("new_film")
                .description("descr")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(120))
                .rating(FilmRating.PG)
                .build();

        var actual = Optional.of(filmStorage.createFilm(film));

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("name", "new_film");
                    assertThat(obj).hasFieldOrPropertyWithValue("description", "descr");
                    assertThat(obj).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 1, 1));
                    assertThat(obj).hasFieldOrPropertyWithValue("duration", Duration.ofMinutes(120));
                    assertThat(obj).hasFieldOrPropertyWithValue("rating", FilmRating.PG);
                });
    }

    @Test
    @Sql({"/test-data.sql"})
    void updateFilm() {
        Film film = Film.builder()
                .id(1L)
                .name("updated_name")
                .description("descr")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(Duration.ofMinutes(120))
                .rating(FilmRating.PG)
                .build();

        var actual = filmStorage.updateFilm(film);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("name", "updated_name");
                    assertThat(obj).hasFieldOrPropertyWithValue("description", "descr");
                    assertThat(obj).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 1, 1));
                    assertThat(obj).hasFieldOrPropertyWithValue("duration", Duration.ofMinutes(120));
                    assertThat(obj).hasFieldOrPropertyWithValue("rating", FilmRating.PG);
                });
    }

    @Test
    @Sql({"/test-data.sql"})
    void getFilmById() {
        var actual = filmStorage.getFilmById(1L);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("name", "film1");
                    assertThat(obj).hasFieldOrPropertyWithValue("description", "descr1");
                    assertThat(obj).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2000, 1, 1));
                    assertThat(obj).hasFieldOrPropertyWithValue("duration", Duration.ofMinutes(120));
                    assertThat(obj).hasFieldOrPropertyWithValue("rating", FilmRating.PG);
                    assertThat(obj).hasFieldOrPropertyWithValue("genres", Set.of(Genre
                            .builder().id(1L).name("Комедия").build()));
                    assertThat(obj).hasFieldOrPropertyWithValue("likes", Set.of(1L));
                });
    }

    @Test
    @Sql({"/test-data.sql"})
    void getMostPopularFilms() {
        var actual = filmStorage.getMostPopularFilms(2);

        assertEquals(2, actual.size());
        assertEquals(3L, actual.get(0).getId());
    }

    @Test
    @Sql({"/test-data.sql"})
    void getFilmsByDirector() {
        var actual = filmStorage.getFilmsByDirector(1L, FilmSort.LIKES);

        assertEquals(1, actual.size());
        assertEquals(1L, actual.get(0).getId());
    }

    @Test
    @Sql({"/test-data.sql"})
    void createFilmLike() {
        filmStorage.createFilmLike(1L, 2L);
        var actual = filmStorage.getFilmById(1L);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("likes", Set.of(1L, 2L));
                });
    }

    @Test
    @Sql({"/test-data.sql"})
    void removeFilmLike() {
        filmStorage.removeFilmLike(1L, 1L);
        var actual = filmStorage.getFilmById(1L);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("likes", Set.of());
                });
    }

    @Test
    @Sql({"/test-data.sql"})
    void addFilmGenres() {
        filmStorage.addFilmGenres(1L, Set.of(2L));
        var actual = filmStorage.getFilmById(1L);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("genres", Set.of(
                            Genre.builder().id(1L).name("Комедия").build(),
                            Genre.builder().id(2L).name("Драма").build()));
                });
    }

    @Test
    @Sql({"/test-data.sql"})
    void addFilmDirectors() {
        filmStorage.addFilmDirectors(2L, Set.of(1L));
        var actual = filmStorage.getFilmById(2L);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(obj).hasFieldOrPropertyWithValue("directors", Set.of(
                            Director.builder().id(1L).name("firstDirector").build(),
                            Director.builder().id(3L).name("thirdDirector").build()));
                });
    }

    @Test
    @Sql({"/test-data.sql"})
    void removeFilmGenres() {
        filmStorage.removeFilmGenres(1L, Set.of(1L));
        var actual = filmStorage.getFilmById(1L);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("genres", Set.of());
                });
    }

    @Test
    @Sql({"/test-data.sql"})
    void removeFilmDirectors() {
        filmStorage.removeFilmDirectors(2L, Set.of(3L));
        var actual = filmStorage.getFilmById(2L);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(obj).hasFieldOrPropertyWithValue("directors", Set.of());
                });
    }
}