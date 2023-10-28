package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        return filmStorage.getAllFilms().stream()
                .filter(f -> f.getName().toLowerCase().contains(lowCaseTitle))
                .collect(Collectors.toList());

    }

    @Override
    public List<Film> getFilmsByDirectorsName(String name) {
        String lowCaseName = name.toLowerCase();
        List<Director> needDirectors = directorStorage.getAllDirectors().stream()
                .filter(d -> d.getName().toLowerCase().contains(lowCaseName))
                .collect(Collectors.toList());

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
}
