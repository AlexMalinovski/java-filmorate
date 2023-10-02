package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.configs.AppProperties;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmRating;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

@RequiredArgsConstructor
@Component
@Primary
public class DbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final AppProperties appProperties;

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("film_name"))
                .description(rs.getString("film_description"))
                .releaseDate(rs.getDate("film_release_date").toLocalDate())
                .duration(Duration.ofMinutes(rs.getLong("film_duration")))
                .rating(FilmRating.valueOf(rs.getString("film_rating")))
                .build();
        Genre genre = makeGenre(rs);
        if (genre.getName() != null) {
            film.addGenre(makeGenre(rs));
        }
        long likerId = rs.getLong("liked_user_id");
        if (likerId != 0) {
            film.addLike(likerId);
        }
        return film;
    }

    private List<Film> mapFilmQueryResult(List<Film> filmQueryResult) {
        final List<Film> buffer = new ArrayList<>();
        filmQueryResult.forEach(f -> {
            int buffSize = buffer.size();
            if (buffSize == 0 || !Objects.equals(buffer.get(buffSize - 1).getId(), f.getId())) {
                buffer.add(f);
                return;
            }
            Film curFilm = buffer.get(buffSize - 1);
            curFilm.addGenre(f.getGenres());
            curFilm.addLike(f.getLikes());
        });
        return buffer;
    }

    protected List<Film> getFilms(int limit, int offset) {
        String sql = "SELECT f.id AS film_id, f.name AS film_name, f.description AS film_description, " +
                "f.release_date AS film_release_date, f.duration AS film_duration, f.rating AS film_rating, " +
                "g.id AS genre_id, g.name AS genre_name, fl.user_id AS liked_user_id " +
                "FROM (SELECT * FROM films LIMIT ? OFFSET ?) AS f " +
                "LEFT JOIN film_genres AS fg ON f.id=fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id=g.id " +
                "LEFT JOIN film_likes AS fl ON f.id=fl.film_id " +
                "ORDER BY film_id";

        List<Film> queryResult = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), limit, offset);
        return mapFilmQueryResult(queryResult);
    }
    @Override
    public List<Film> getAllFilms() {
        return getFilms(100, 0); //параметры - для будущей пагинации
    }

    @Override
    public Film createFilm(Film film) {
        Map<String, Object> row = new HashMap<>();
        row.put("name", film.getName());
        row.put("description", film.getDescription());
        row.put("release_date", film.getReleaseDate().format(appProperties.getDefaultDateFormatter()));
        row.put("duration", film.getDuration().toMinutes());
        row.put("rating", film.getRating().name());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("id");
        final long id = simpleJdbcInsert.executeAndReturnKey(row).longValue();
        return getFilmById(id)
                .orElseThrow(() -> new IllegalStateException("Ошибка при добавлении записи в БД"));
    }

    @Override
    public Optional<Film> updateFilm(Film filmUpdates) {
        String sql = "UPDATE films SET name=?, description=?, release_date=?, duration=?, rating=? WHERE id=?";
        jdbcTemplate.update(sql,
                filmUpdates.getName(),
                filmUpdates.getDescription(),
                filmUpdates.getReleaseDate().format(appProperties.getDefaultDateFormatter()),
                filmUpdates.getDuration().toMinutes(),
                filmUpdates.getRating().name(),
                filmUpdates.getId());
        return getFilmById(filmUpdates.getId());
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        String sql = "SELECT f.id AS film_id, f.name AS film_name, f.description AS film_description, " +
                "f.release_date AS film_release_date, f.duration AS film_duration, f.rating AS film_rating, " +
                "g.id AS genre_id, g.name AS genre_name, fl.user_id AS liked_user_id " +
                "FROM films AS f " +
                "LEFT JOIN film_genres AS fg ON f.id=fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id=g.id " +
                "LEFT JOIN film_likes AS fl ON f.id=fl.film_id " +
                "WHERE f.id=?";
        List<Film> queryResult = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        if (queryResult.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(mapFilmQueryResult(queryResult).get(0));
    }

    @Override
    public List<Film> getMostPopularFilms(int count) {
        String sql = "SELECT topf.*, g.id AS genre_id, g.name AS genre_name, fl.user_id AS liked_user_id \n" +
                "FROM (SELECT f.id AS film_id, f.name AS film_name, f.description AS film_description, \n" +
                    "f.release_date AS film_release_date, f.duration AS film_duration, f.rating AS film_rating, \n" +
                    "count(fl.user_id) AS cnt\n" +
                    "FROM films AS f LEFT JOIN film_likes AS fl ON f.id=fl.film_id\n" +
                    "GROUP BY film_id, film_name, film_description, film_release_date, film_duration, film_rating\n" +
                    "ORDER BY cnt DESC\n" +
                    "LIMIT ?) AS topf \n" +
                "LEFT JOIN film_genres AS fg ON topf.film_id=fg.film_id\n" +
                "LEFT JOIN genres AS g ON fg.genre_id=g.id\n" +
                "LEFT JOIN film_likes AS fl ON topf.film_id=fl.film_id";

        List<Film> queryResult = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
        return mapFilmQueryResult(queryResult);
    }

    @Override
    public void createFilmLike(long filmId, long userId) {
        Map<String, Object> row = new HashMap<>();
        row.put("film_id", filmId);
        row.put("user_id", userId);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film_likes");
        simpleJdbcInsert.execute(row);
    }

    @Override
    public void removeFilmLike(long filmId, long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id=? AND user_id=?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void addFilmGenres(long id, Set<Long> foundGenresId) {
        final List<Map<String, Object>> rows = new ArrayList<>();
        foundGenresId.forEach(gid -> rows.add(Map.of("film_id", id, "genre_id", gid)));

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film_genres");
        simpleJdbcInsert.executeBatch(SqlParameterSourceUtils.createBatch(rows));
    }

    @Override
    public void removeFilmGenres(long id, Set<Long> genresToRemove) {
        if (genresToRemove.size() == 0) {
            return;
        }
        String inSql = String.join(",", Collections.nCopies(genresToRemove.size(), "?"));
        String sql = "DELETE FROM film_genres WHERE film_id=? AND genre_id IN (%s)";
        jdbcTemplate.update(String.format(sql, inSql), id, genresToRemove.toArray());
    }
}
