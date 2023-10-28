package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmLike;
import ru.yandex.practicum.filmorate.storages.FilmStorage;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SimpleRecommendationService implements RecommendationService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Override
    @Transactional(readOnly = true)
    public List<Film> getRecommendations(final long id) {
        final int maxUsersCheck = 2;
        userStorage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь id=" + id));

        List<FilmLike> allFilmLikes = filmStorage.getAllFilmLikes();
        final Map<Long, Set<Long>> usersLikes = allFilmLikes
                .stream()
                .collect(Collectors.groupingBy(FilmLike::getUserId,
                        Collectors.mapping(FilmLike::getFilmId, Collectors.toSet())));
        if (!usersLikes.containsKey(id)) {
            return new ArrayList<>(); // пользователь ничего не лайкал -> нечего рекомендовать
        }
        final Set<Long> currentUserLikes = Collections.unmodifiableSet(usersLikes.getOrDefault(id, new HashSet<>()));

        List<Pair<Long, Integer>> intersectedUsers = usersLikes.entrySet()
                .stream()
                .map(entry -> {
                    Set<Long> intersectSet = new HashSet<>(entry.getValue());
                    intersectSet.retainAll(currentUserLikes);
                    return Pair.of(entry.getKey(), intersectSet.size());
                })
                .filter(p -> p.getFirst() != id && p.getSecond() > 0)
                .sorted((p1, p2) -> Integer.compare(p2.getSecond(), p1.getSecond()))
                .limit(maxUsersCheck)
                .collect(Collectors.toList());
        if (intersectedUsers.isEmpty()) {
            return new ArrayList<>(); // нет пользователей с пересекающимися лайками
        }

        Set<Long> intersectedUsersFilms = new HashSet<>();
        for (Pair<Long, Integer> intersectedUser : intersectedUsers) {
            intersectedUsersFilms.addAll(usersLikes.get(intersectedUser.getFirst()));
        }
        intersectedUsersFilms.removeAll(currentUserLikes);
        if (intersectedUsersFilms.isEmpty()) {
            return new ArrayList<>(); // нечего рекомендовать, т.к. все пролайкали одни и те-же фильмы
        }

        return filmStorage.getFilmsByIds(intersectedUsersFilms);
    }
}
