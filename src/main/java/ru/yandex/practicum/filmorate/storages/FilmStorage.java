package ru.yandex.practicum.filmorate.storages;

import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmLike;
import ru.yandex.practicum.filmorate.models.FilmSort;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film createFilm(@NonNull Film film);

    Optional<Film> updateFilm(@NonNull Film filmUpdates);

    Optional<Film> getFilmById(long id);

    List<Film> getFilmsByIds(@NonNull Set<Long> ids);

    List<Film> getMostPopularFilms(int count);

    List<Film> getFilmsByDirector(long directorId, FilmSort sort);

    void createFilmLike(long filmId, long userId);

    void removeFilmLike(long filmId, long userId);

    List<FilmLike> getAllFilmLikes();

    void addFilmGenres(long id, @NonNull Set<Long> foundGenresId);

    void addFilmDirectors(long id, Set<Long> foundDirectors);

    void removeFilmGenres(long id, @NonNull Set<Long> genresToRemove);

    void removeFilmDirectors(long id, Set<Long> directorsToRemove);
}
