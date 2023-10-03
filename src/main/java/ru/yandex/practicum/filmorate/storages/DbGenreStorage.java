package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Repository
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
        String sql = "select id as genre_id, name as genre_name from genres";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    @Override
    public Optional<Genre> getGenreById(long id) {
        String sql = "select id as genre_id, name as genre_name from genres where id=?";
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
        String sql = "select id as genre_id, name as genre_name from genres where id in (%s)";

        return jdbcTemplate.query(String.format(sql, inSql),
                (rs, rowNum) -> makeGenre(rs), genresId.toArray());
    }
}
