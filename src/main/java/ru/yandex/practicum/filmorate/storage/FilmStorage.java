package ru.yandex.practicum.filmorate.storage;

import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

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
}
