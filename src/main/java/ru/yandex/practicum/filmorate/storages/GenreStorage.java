package ru.yandex.practicum.filmorate.storages;

import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.models.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    List<Genre> getAllGenres();

    Optional<Genre> getGenreById(long id);

    List<Genre> getGenresById(@NonNull Set<Long> genresId);
}
