package ru.yandex.practicum.filmorate.storage;

import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {
    @NonNull
    List<Genre> getAllGenres();

    @NonNull
    Optional<Genre> getGenreById(long id);

    @NonNull
    List<Genre> getGenresById(@NonNull Set<Long> genresId);
}
