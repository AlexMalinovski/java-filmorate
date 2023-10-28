package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.utils.AppProperties;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmRating;
import ru.yandex.practicum.filmorate.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Repository
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
        String sql = "select f.id as film_id, f.name as film_name, f.description as film_description, " +
                "f.release_date as film_release_date, f.duration as film_duration, f.rating as film_rating, " +
                "g.id as genre_id, g.name as genre_name, fl.user_id as liked_user_id " +
                "from (select * from films limit ? offset ?) as f " +
                "left join film_genres as fg on f.id=fg.film_id " +
                "left join genres as g on fg.genre_id=g.id " +
                "left join film_likes as fl on f.id=fl.film_id " +
                "order by film_id";

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
        String sql = "update films set name=?, description=?, release_date=?, duration=?, rating=? where id=?";
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
        String sql = "select f.id as film_id, f.name as film_name, f.description as film_description, " +
                "f.release_date as film_release_date, f.duration as film_duration, f.rating as film_rating, " +
                "g.id as genre_id, g.name as genre_name, fl.user_id as liked_user_id " +
                "from films as f " +
                "left join film_genres as fg on f.id=fg.film_id " +
                "left join genres as g on fg.genre_id=g.id " +
                "left join film_likes as fl on f.id=fl.film_id " +
                "where f.id=?";
        List<Film> queryResult = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        if (queryResult.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(mapFilmQueryResult(queryResult).get(0));
    }

    @Override
    public List<Film> getMostPopularFilms(int count, Long genreId, Integer year) {
        List<Film> popularMovies = new ArrayList<>();
        if (genreId == null && year == null) {
            String sqlQuery = "select * from films "
                    + "left join film_likes on film_likes.film_id = films.film_id "
                    + "group by films.film_id "
                    + "order by count (film_likes.film_id) desc "
                    + "limit "
                    + count;
            popularMovies = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), count);
            return mapFilmQueryResult(popularMovies);
        }
        if (genreId != null && year == null) {
            String sqlQuery = "select * from films "
                    + "left join film_likes on film_likes.film_id = films.id "
                    + "join film_genres on films.id = film_genres.film_id "
                    + "where film_genres.genre_id = " + genreId + " "
                    + "group by films.id "
                    + "order by count (film_likes.film_id) desc "
                    + "limit "
                    + count;

            popularMovies = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), count);
            return mapFilmQueryResult(popularMovies);
        }
        if (genreId != null && year != null) {
            String sqlQuery = "select * from films "
                    + "left join film_likes on film_likes.film_id = films.id "
                    + "join film_genres on films.id = film_genres.film_id "
                    + "where film_genres.genre_id = " + genreId
                    + " and (extract(year from cast(films.release_date as date))) = " + year
                    + "group by films.id "
                    + "order by count (film_likes.film_id) desc "
                    + "limit "
                    + count;

            popularMovies = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), count);
            return mapFilmQueryResult(popularMovies);
        }
        if (genreId == null && year != null) {
            String sqlQuery = "select * from films "
                    + "left join film_likes on film_likes.film_id = films.id "
                    + "where (extract(year from cast(films.release_date as date))) = " + year
                    + "group by films.id "
                    + "order by count (film_likes.film_id) desc "
                    + "limit "
                    + count;

            popularMovies = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), count);
            return mapFilmQueryResult(popularMovies);
        }
        return  popularMovies;
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
        String sql = "delete from film_likes where film_id=? and user_id=?";
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
        String sql = "delete from film_genres where film_id=? and genre_id in (%s)";
        jdbcTemplate.update(String.format(sql, inSql), id, genresToRemove.toArray());
    }
}
