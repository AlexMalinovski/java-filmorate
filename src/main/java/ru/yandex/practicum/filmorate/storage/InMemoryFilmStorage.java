package ru.yandex.practicum.filmorate.storage;

import lombok.*;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(AccessLevel.PACKAGE)
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films;
    private final AtomicLong currentId;

    public InMemoryFilmStorage() {
        this.films = new ConcurrentHashMap<>();
        this.currentId = new AtomicLong(1L);
    }

    @Override
    public List<Film> getAllFilms() {
        return films.values().stream()
                .map(Film::copyOf)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Film createFilm(Film film) {
        final Film createdFilm = film.copyOf();
        createdFilm.setId(currentId.getAndIncrement());
        films.put(createdFilm.getId(), createdFilm);
        return createdFilm.copyOf();
    }

    @Override
    public Optional<Film> updateFilm(Film filmUpdates) {
        return Optional.ofNullable(films.computeIfPresent(filmUpdates.getId(), (k, v) -> filmUpdates.copyOf()))
                .map(Film::copyOf);
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        return Optional.ofNullable(films.get(id))
                .map(Film::copyOf);
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        return films.values()
                .stream()
                .sorted(Comparator.comparingInt(Film::getNumOfLikes).reversed())
                .limit(count)
                .map(Film::copyOf)
                .collect(Collectors.toList());
    }
}
