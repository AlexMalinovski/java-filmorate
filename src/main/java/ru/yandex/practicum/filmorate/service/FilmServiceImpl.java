package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    public List<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    @Override
    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    @Override
    public Optional<Film> updateFilm(Film filmUpdates) {
        return filmStorage.updateFilm(filmUpdates);
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        return filmStorage.getFilmById(id);
    }

    @Override
    public Film likeFilm(long filmId, long userId) throws NotFoundException {
        final Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Не найден фильм с id:" + filmId));
        final User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id:" + userId));
        film.addLike(user.getId());
        return filmStorage.updateFilm(film)
                .orElseThrow(() -> new NotFoundException("Фильм был удалён другим пользователем"));
    }

    @Override
    public Film unlikeFilm(long filmId, long userId) throws NotFoundException {
        final Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Не найден фильм с id:" + filmId));
        final User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id:" + filmId));
        film.removeLike(user.getId());
        return filmStorage.updateFilm(film)
                .orElseThrow(() -> new NotFoundException("Фильм был удалён другим пользователем"));
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        return filmStorage.getMostPopularFilms(count);
    }
}
