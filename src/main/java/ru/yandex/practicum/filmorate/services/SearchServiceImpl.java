package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Director;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmSort;
import ru.yandex.practicum.filmorate.storages.DirectorStorage;
import ru.yandex.practicum.filmorate.storages.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;


    @Override
    public List<Film> getFilmsByTitle(String title) {
        String lowCaseTitle = title.toLowerCase();
        return filmStorage.getFilmsByTitle(lowCaseTitle);

    }

    @Override
    public List<Film> getFilmsByDirectorsName(String name) {
        String lowCaseName = name.toLowerCase();
        List<Director> needDirectors = directorStorage.getDirectorsByName(lowCaseName);

        List<Film> films = new ArrayList<>();

        for (Director director : needDirectors) {
            films.addAll(filmStorage.getFilmsByDirector(director.getId(), FilmSort.LIKES));
        }
        //для проверки, что фильмы не повторяются
        Set<Film> result = new HashSet<>(films);

        return new ArrayList<>(result);
    }

    @Override
    public List<Film> getFilmsByDirectorAndTitle(String str) {
        Set<Film> films = new HashSet<>();

        films.addAll(getFilmsByDirectorsName(str));
        films.addAll(getFilmsByTitle(str));

        return films.stream()
                .sorted(Comparator.comparingLong(Film::getId).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getFilmsBySearchParams(String by, String query) {
        switch (by) {
            case "director":
                return getFilmsByDirectorsName(query);
            case "title":
                return getFilmsByTitle(query);
            case "director,title":
            case "title,director":
                return getFilmsByDirectorAndTitle(query);
            default:
                throw new NotFoundException("Некорректный параметр поиска");
        }
    }
}
