package ru.yandex.practicum.filmorate.services;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmSort;
import ru.yandex.practicum.filmorate.models.Genre;

import java.util.List;
import java.util.Optional;

public interface FilmService {
    @NonNull
    List<Film> getFilms();

    @NonNull
    Film createFilm(@NonNull Film film);

    @NonNull
    Optional<Film> updateFilm(@NonNull Film filmUpdates);

    @NonNull
    Optional<Film> getFilmById(long id);

    @NonNull
    Film likeFilm(long filmId, long userId) throws NotFoundException;

    @NonNull
    Film unlikeFilm(long filmId, long userId) throws NotFoundException;

    @NonNull
    List<Film> getMostPopularFilms(int count, @Nullable Long genreId, @Nullable Integer year);

    @NonNull
    List<Film> getFilmsByDirector(long directorId, FilmSort sort);

    @NonNull
    List<Genre> getGenres();

    @NonNull
    Optional<Genre> getGenreById(long id);
}
