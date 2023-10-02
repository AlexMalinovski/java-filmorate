package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
@Component
@Primary
public class DbGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return Genre.builder()
                .id(rs.getLong("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "SELECT id AS genre_id, name AS genre_name FROM genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Optional<Genre> getGenreById(long id) {
        String sql = "SELECT id AS genre_id, name AS genre_name FROM genres WHERE id=?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), id));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> getGenresById(Set<Long> genresId) {
        if (genresId.size() == 0) {
            return new ArrayList<>();
        }
        String inSql = String.join(",", Collections.nCopies(genresId.size(), "?"));
        String sql = "SELECT id AS genre_id, name AS genre_name FROM genres WHERE id IN (%s)";

        return jdbcTemplate.query(String.format(sql, inSql),
                (rs, rowNum) -> makeGenre(rs), genresId.toArray());
    }
}
