package ru.yandex.practicum.filmorate.storages;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.Genre;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
@Slf4j
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
    public List<Film> getMostPopularFilms(int count, Long genreId, Integer year) {

        if (isGenreIdContained(genreId) && isYearContained(year)) {
            return films.values()
                    .stream()
                    .filter(film -> film.getGenres().stream().filter(genre -> Objects.equals(genre.getId(), genreId)).isParallel()
                            && Objects.equals(film.getReleaseDate().getYear(), year))
                    .sorted(Comparator.comparingInt(Film::getNumOfLikes).reversed())
                    .limit(count)
                    .map(Film::copyOf)
                    .collect(Collectors.toList());
        } else if (isGenreIdContained(genreId) && !isYearContained(year)) {
            return films.values()
                    .stream()
                    .filter(film -> film.getGenres().stream().filter(genre -> Objects.equals(genre.getId(), genreId)).isParallel())
                    .sorted(Comparator.comparingInt(Film::getNumOfLikes).reversed())
                    .limit(count)
                    .map(Film::copyOf)
                    .collect(Collectors.toList());
        } else if (!isGenreIdContained(genreId) && isYearContained(year)) {
            return films.values()
                    .stream()
                    .filter(film -> Objects.equals(film.getReleaseDate().getYear(), year))
                    .sorted(Comparator.comparingInt(Film::getNumOfLikes).reversed())
                    .limit(count)
                    .map(Film::copyOf)
                    .collect(Collectors.toList());
        }
        return films.values()
                .stream()
                .sorted(Comparator.comparingInt(Film::getNumOfLikes).reversed())
                .limit(count)
                .map(Film::copyOf)
                .collect(Collectors.toList());
    }

    @Override
    public void createFilmLike(long filmId, long userId) {
        films.computeIfPresent(filmId, (id, film) -> {
            film.addLike(userId);
            return film;
        });
    }

    @Override
    public void removeFilmLike(long filmId, long userId) {
        films.computeIfPresent(filmId, (id, film) -> {
            film.removeLike(userId);
            return film;
        });
    }

    @Override
    public void addFilmGenres(long id, Set<Long> foundGenresId) {
        throw new IllegalStateException("Not implemented");
    }

    @Override
    public void removeFilmGenres(long id, Set<Long> genresToRemove) {
        throw new IllegalStateException("Not implemented");
    }

    public boolean isGenreIdContained(Long genreId) {
        for (Film film : films.values())
            for (Genre g : film.getGenres()) {
                if (g.getId().equals(genreId)) {
                    return true;
                }
            }
        return false;
    }

    public boolean isYearContained(Integer year) {
        for (Film film : films.values())
            if (Objects.equals(film.getReleaseDate().getYear(), year)) {
                return true;
            }
        return false;
    }
}
