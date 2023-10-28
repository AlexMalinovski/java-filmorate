package ru.yandex.practicum.filmorate.storages;

import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film createFilm(@NonNull Film film);

    Optional<Film> updateFilm(@NonNull Film filmUpdates);

    Optional<Film> getFilmById(long id);

    List<Film> getMostPopularFilms(int count, Long genreId, Integer year);

    void createFilmLike(long filmId, long userId);

    void removeFilmLike(long filmId, long userId);

    void addFilmGenres(long id, @NonNull Set<Long> foundGenresId);

    void removeFilmGenres(long id, @NonNull Set<Long> genresToRemove);
}
