package ru.yandex.practicum.filmorate.storages;

import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.models.Director;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface DirectorStorage {
    List<Director> getAllDirectors();

    Director createDirector(@NonNull Director director);

    Optional<Director> updateDirector(@NonNull Director directorUpdates);

    Optional<Director> getDirectorById(long id);

    List<Director> getDirectorsById(@NonNull Set<Long> directorsId);

    void deleteDirectorById(long id);

    List<Director> getDirectorsByName(String name);
}
