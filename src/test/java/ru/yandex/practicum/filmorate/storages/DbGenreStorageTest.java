package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DbGenreStorageTest {

    @Autowired
    @Qualifier("dbGenreStorage")
    private GenreStorage genreStorage;

    @Test
    @Sql({"/data-clear.sql"})
    void getAllGenres() {
        var actual = genreStorage.getAllGenres();

        assertEquals(6, actual.size());
    }

    @Test
    @Sql({"/data-clear.sql"})
    void getGenreById() {
        var actual = genreStorage.getGenreById(1L);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("name", "Комедия");
                });
    }

    @Test
    @Sql({"/data-clear.sql"})
    void getGenresById() {
        var actual = genreStorage.getGenresById(Set.of(1L));

        assertEquals(1, actual.size());
        assertEquals(1L, actual.get(0).getId());
    }
}