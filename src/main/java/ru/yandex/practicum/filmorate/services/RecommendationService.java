package ru.yandex.practicum.filmorate.services;

import org.springframework.lang.NonNull;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;

public interface RecommendationService {
    @NonNull
    List<Film> getRecommendations(long id);
}
