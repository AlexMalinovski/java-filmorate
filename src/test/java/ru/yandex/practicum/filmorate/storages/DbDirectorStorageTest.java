package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.models.Director;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DbDirectorStorageTest {

    @Autowired
    @Qualifier("dbDirectorStorage")
    private DirectorStorage directorStorage;

    @Test
    @Sql({"/data-clear.sql"})
    @Sql({"/test-data.sql"})
    void getAllDirectors() {
        List<Director> directors = directorStorage.getAllDirectors();

        assertEquals(3, directors.size());
    }

    @Test
    @Sql({"/data-clear.sql"})
    @Sql({"/test-data.sql"})
    void getDirectorById() {
        Optional<Director> director = directorStorage.getDirectorById(1L);

        assertThat(director)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("name", "firstDirector");
                });
    }

    @Test
    @Sql({"/data-clear.sql"})
    @Sql({"/test-data.sql"})
    void getDirectorsById() {
        List<Director> directors = directorStorage.getDirectorsById(Set.of(1L, 2L));

        assertEquals(2, directors.size());
        assertEquals(1L, directors.get(0).getId());
        assertEquals(2L, directors.get(1).getId());
    }

    @Test
    @Sql({"/data-clear.sql"})
    void createDirector() {
        Director director = Director.builder()
                .name("newDirector")
                .build();

        var createdDirector = Optional.of(directorStorage.createDirector(director));

        assertThat(createdDirector)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("name", "newDirector");
                });
    }

    @Test
    @Sql({"/data-clear.sql"})
    @Sql({"/test-data.sql"})
    void updateDirector() {
        Director director = Director.builder()
                .id(1L)
                .name("newDirector")
                .build();

        var updateDirector = directorStorage.updateDirector(director);

        assertThat(updateDirector)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("name", "newDirector");
                });
    }

    @Test
    @Sql({"/data-clear.sql"})
    @Sql({"/test-data.sql"})
    void deleteDirector() {
        directorStorage.deleteDirectorById(1L);
        List<Director> directors = directorStorage.getAllDirectors();

        assertEquals(2, directors.size());
    }


}