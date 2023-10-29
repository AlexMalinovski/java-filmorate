package ru.yandex.practicum.filmorate.services;

import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.models.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorService {

    @NonNull
    List<Director> getDirectors();

    @NonNull
    Director createDirector(@NonNull Director director);

    @NonNull
    Optional<Director> updateDirector(@NonNull Director directorUpdates);

    @NonNull
    Director getDirectorById(long id);

    @NonNull
    void deleteDirectorById(long id);
}
