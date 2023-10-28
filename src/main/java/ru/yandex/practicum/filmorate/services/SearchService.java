package ru.yandex.practicum.filmorate.services;


import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;

public interface SearchService {

    @NonNull
    public List<Film> getFilmsByTitle(String title);

    @NonNull
    public List<Film> getFilmsByDirectorsName(String name);

    @NonNull
    public List<Film> getFilmsByDirectorAndTitle(String str);

    List<Film> getFilmsBySearchParams(String by, String query);
}
