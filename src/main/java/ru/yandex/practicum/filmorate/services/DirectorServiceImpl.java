package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.storages.DirectorStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorStorage directorStorage;

    @Override
    public List<Director> getDirectors() {
        return directorStorage.getAllDirectors();
    }

    @Override
    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    @Override
    public Optional<Director> updateDirector(Director directorUpdates) {
        return directorStorage.updateDirector(directorUpdates);
    }

    @Override
    public Director getDirectorById(long id) {
        return directorStorage.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Данный режиссер не найден"));
    }

    @Override
    public void deleteDirectorById(long id) {
        directorStorage.deleteDirectorById(id);
    }
}
