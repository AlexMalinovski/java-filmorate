package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmSort;
import ru.yandex.practicum.filmorate.models.Genre;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.DirectorStorage;
import ru.yandex.practicum.filmorate.storages.FilmStorage;
import ru.yandex.practicum.filmorate.storages.GenreStorage;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;

    @Override
    public List<Film> getFilms() {
        return filmStorage.getAllFilms();
    }

    @Transactional
    @Override
    public Film createFilm(Film film) {
        Set<Long> genresId = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        Set<Genre> foundGenres = null;
        if (genresId.size() > 0) {
            foundGenres = new HashSet<>(genreStorage.getGenresById(genresId));
            if (foundGenres.size() != genresId.size()) {
                throw new NotFoundException("Переданы несуществующие id жанров");
            }
        }

        Set<Long> directorsId = film.getDirectors().stream()
                .map(Director::getId)
                .collect(Collectors.toSet());
        Set<Director> foundDirectors = null;
        if (directorsId.size() > 0) {
            foundDirectors = new HashSet<>(directorStorage.getDirectorsById(directorsId));
            if (foundDirectors.size() != genresId.size()) {
                throw new NotFoundException("Переданы несуществующие id режиссеров");
            }
        }

        Film createdFilm = filmStorage.createFilm(film);
        if (foundGenres != null && foundGenres.size() > 0) {
            Set<Long> foundGenresId = foundGenres
                    .stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            filmStorage.addFilmGenres(createdFilm.getId(), foundGenresId);
            createdFilm.setGenres(foundGenres);
        }

        if (foundDirectors != null && foundDirectors.size() > 0) {
            Set<Long> foundDirectorsId = foundDirectors
                    .stream()
                    .map(Director::getId)
                    .collect(Collectors.toSet());
            filmStorage.addFilmDirectors(createdFilm.getId(), foundDirectorsId);
            createdFilm.setDirectors(foundDirectors);
        }
        return createdFilm;
    }

    @Transactional
    @Override
    public Optional<Film> updateFilm(Film filmUpdates) {
        Set<Long> genresId = filmUpdates.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        Set<Genre> updateGenres = new HashSet<>();
        if (genresId.size() > 0) {
            updateGenres.addAll(genreStorage.getGenresById(genresId));
            if (updateGenres.size() != genresId.size()) {
                throw new NotFoundException("Переданы несуществующие id жанров");
            }
        }

        Set<Long> directorsId = filmUpdates.getDirectors().stream()
                .map(Director::getId)
                .collect(Collectors.toSet());
        Set<Director> updateDirectors = new HashSet<>();
        if (directorsId.size() > 0) {
            updateDirectors.addAll(directorStorage.getDirectorsById(directorsId));
            if (updateDirectors.size() != directorsId.size()) {
                throw new NotFoundException("Переданы несуществующие id режиссеров");
            }
        }

        Optional<Film> updatedFilm = filmStorage.updateFilm(filmUpdates);
        if (updatedFilm.isEmpty()) {
            return updatedFilm;
        }

        Set<Long> updateGenresId = updateGenres
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        Set<Long> currentGenresId = updatedFilm
                .get()
                .getGenres()
                .stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());
        Set<Long> intersectGenres = new HashSet<>(updateGenresId);
        intersectGenres.retainAll(currentGenresId);
        final Set<Long> genresToRemove = new HashSet<>(currentGenresId);
        genresToRemove.removeAll(intersectGenres);
        final Set<Long> genresToAdd = new HashSet<>(updateGenresId);
        genresToAdd.removeAll(intersectGenres);

        Set<Long> updateDirectorsId = updateDirectors
                .stream()
                .map(Director::getId)
                .collect(Collectors.toSet());
        Set<Long> currentDirectorsId = updatedFilm
                .get()
                .getDirectors()
                .stream()
                .map(Director::getId)
                .collect(Collectors.toSet());
        Set<Long> intersectDirectors = new HashSet<>(updateDirectorsId);
        intersectDirectors.retainAll(currentDirectorsId);
        final Set<Long> directorsToRemove = new HashSet<>(currentDirectorsId);
        directorsToRemove.removeAll(intersectDirectors);
        final Set<Long> directosToAdd = new HashSet<>(updateDirectorsId);
        directosToAdd.removeAll(intersectDirectors);

        filmStorage.addFilmGenres(filmUpdates.getId(), genresToAdd);
        filmStorage.removeFilmGenres(filmUpdates.getId(), genresToRemove);
        filmStorage.addFilmDirectors(filmUpdates.getId(), directosToAdd);
        filmStorage.removeFilmDirectors(filmUpdates.getId(), directorsToRemove);
        return filmStorage.getFilmById(filmUpdates.getId());
    }

    @Override
    public Optional<Film> getFilmById(long id) {

        return filmStorage.getFilmById(id);
    }

    @Transactional
    @Override
    public Film likeFilm(long filmId, long userId) throws NotFoundException {
        final Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Не найден фильм с id:" + filmId));
        final User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id:" + userId));
        if (film.addLike(user.getId())) {
            filmStorage.createFilmLike(filmId, userId);
        }
        return film;
    }

    @Transactional
    @Override
    public Film unlikeFilm(long filmId, long userId) throws NotFoundException {
        final Film film = filmStorage.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Не найден фильм с id:" + filmId));
        final User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id:" + userId));
        if (film.removeLike(user.getId())) {
            filmStorage.removeFilmLike(filmId, userId);
        }
        return film;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Film> getMostPopularFilms(int count, Long genreId, Integer year) {
        return filmStorage.getMostPopularFilms(count, genreId, year);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Film> getFilmsByDirector(long directorId, FilmSort sort) {

        directorStorage.getDirectorById(directorId)
                .orElseThrow(() -> new NotFoundException("Не найден режиссер с id:" + directorId));

        return filmStorage.getFilmsByDirector(directorId, sort);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Genre> getGenres() {
        return genreStorage.getAllGenres();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Genre> getGenreById(long id) {
        return genreStorage.getGenreById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Film> getCommonFilms(long userId, long friendId) {
        userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id:" + userId));
        userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id:" + friendId));

        Set<Long> userFilmLikes = filmStorage.getUserFilmLikes(userId);
        Set<Long> friendFilmLikes = filmStorage.getUserFilmLikes(friendId);
        Set<Long> commonFilmLikes = new HashSet<>(userFilmLikes);
        commonFilmLikes.retainAll(friendFilmLikes);
        if (commonFilmLikes.isEmpty()) {
            return new ArrayList<>();
        }
        return filmStorage.getFilmsByIds(commonFilmLikes)
                .stream()
                .sorted(Comparator.comparingInt(Film::getNumOfLikes).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Film deleteFilmById(long id) throws NotFoundException, IllegalStateException {
        final Film film = filmStorage.getFilmById(id)
                .orElseThrow(() -> new NotFoundException("Не найден фильм с id: " + id));
        filmStorage.deleteFilmById(id);
        return film;
    }
}
