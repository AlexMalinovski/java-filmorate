package ru.yandex.practicum.filmorate.storage;

import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    @NonNull
    List<Film> getAllFilms();

    @NonNull
    Film createFilm(@NonNull Film film);

    @NonNull
    Optional<Film> updateFilm(@NonNull Film filmUpdates);

    @NonNull
    Optional<Film> getFilmById(long id);

    @NonNull
    List<Film> getMostPopularFilms(int count);

    void createFilmLike(long filmId, long userId);

    void removeFilmLike(long filmId, long userId);

    void addFilmGenres(long id, @NonNull Set<Long> foundGenresId);

    void removeFilmGenres(long id, @NonNull Set<Long> genresToRemove);
}
