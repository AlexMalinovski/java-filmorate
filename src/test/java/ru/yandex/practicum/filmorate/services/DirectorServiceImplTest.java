package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.storages.DirectorStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DirectorServiceImplTest {
    @Mock
    private DirectorStorage directorStorage;

    @InjectMocks
    private DirectorServiceImpl directorService;

    @Test
    void getDirectors_whenNotFound_thenReturnEmptyList() {
        when(directorStorage.getAllDirectors()).thenReturn(new ArrayList<>());

        List<Director> directors = directorService.getDirectors();

        verify(directorStorage).getAllDirectors();
        assertNotNull(directors);
        assertTrue(directors.isEmpty());
    }

    @Test
    void getDirectors_ifFounded_thenReturnFounded() {
        var expected = Director.builder().id(1L).name("name").build();
        when(directorStorage.getAllDirectors()).thenReturn(List.of(expected));

        var actual = directorService.getDirectors();

        verify(directorStorage).getAllDirectors();
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertSame(expected, actual.get(0));
    }

    @Test
    void createDirector_ifCreate_thenReturnCreated() {
        var expected = Director.builder().id(1L).name("name").build();
        when(directorStorage.createDirector(expected)).thenReturn(expected);

        var actual = directorService.createDirector(expected);

        verify(directorStorage).createDirector(expected);
        assertSame(expected, actual);
    }

    @Test
    void updateDirector_ifNotFound_thenReturnEmptyOptional() {
        var expected = Director.builder().id(1L).name("name").build();
        when(directorStorage.updateDirector(expected)).thenReturn(Optional.empty());

        var actual = directorService.updateDirector(expected);

        verify(directorStorage).updateDirector(expected);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void updateDirector_ifFounded_thenReturnUpdatedOptional() {
        var expected = Director.builder().id(1L).name("name").build();
        when(directorStorage.updateDirector(expected)).thenReturn(Optional.of(expected));
        when(directorStorage.getDirectorById(expected.getId())).thenReturn(Optional.of(expected));

        var actual = directorService.updateDirector(expected);

        verify(directorStorage).updateDirector(expected);
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertSame(expected, actual.get());
    }
}