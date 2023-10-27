package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.models.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Repository
@Primary
public class DbDirectorStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    private Director makeDirector(ResultSet rs) throws SQLException {
        return Director.builder()
                .id(rs.getLong("director_id"))
                .name(rs.getString("director_name"))
                .build();
    }

    @Override
    public List<Director> getAllDirectors() {
        String sql = "select id as director_id, name as director_name from directors";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs));
    }

    @Override
    public Director createDirector(Director director) {
        Map<String, Object> row = new HashMap<>();
        row.put("id", director.getId());
        row.put("name", director.getName());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("directors")
                .usingGeneratedKeyColumns("id");
        final long id = simpleJdbcInsert.executeAndReturnKey(row).longValue();
        director.setId(id);

        return director;
    }

    @Override
    public Optional<Director> updateDirector(Director directorUpdates) {
        String sql = "update directors set name = ? where id = ? ";
        jdbcTemplate.update(sql,
                directorUpdates.getName(),
                directorUpdates.getId());
        return getDirectorById(directorUpdates.getId());
    }

    @Override
    public Optional<Director> getDirectorById(long id) {
        String sql = "select id as director_id, name as director_name from directors where id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeDirector(rs), id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Director> getDirectorsById(Set<Long> directorsId) {
        if (directorsId.size() == 0) {
            return new ArrayList<>();
        }
        String inSql = String.join(",", Collections.nCopies(directorsId.size(), "?"));
        String sql = "select id as director_id, name as director_name from directors where id in (%s)";

        return jdbcTemplate.query(String.format(sql, inSql),
                (rs, rowNum) -> makeDirector(rs), directorsId.toArray());
    }

    @Override
    public void deleteDirectorById(long id) {
        String sql = "delete from directors where id = ?";

        jdbcTemplate.update(sql, id);
    }


}
