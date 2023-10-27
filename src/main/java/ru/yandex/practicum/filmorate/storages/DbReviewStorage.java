package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.models.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
@Primary
public class DbReviewStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    private Review makeReview(ResultSet rs) throws SQLException {
        return Review.builder()
                .id(rs.getLong("review_id"))
                .userId(rs.getLong("author_id"))
                .filmId(rs.getLong("film_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .useful(rs.getLong("useful"))
                .build();
    }

    @Override
    public List<Review> getReviews(Long filmId, int count) {
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("film_id", filmId)
                .addValue("count", count);
        String sql = "select r.id as review_id, r.film_id as film_id, r.user_id as author_id, " +
                "r.content as content, r.is_positive as is_positive, " +
                "coalesce(sum(reaction.reaction), 0) as useful " +
                "from reviews as r " +
                "left join review_reactions as reaction on r.id=reaction.review_id " +
                "where (:film_id is null) or r.film_id=:film_id " +
                "group by review_id, film_id, author_id, content, is_positive " +
                "order by useful desc " +
                "limit :count";

        return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> makeReview(rs));
    }

    @Override
    public Review createReview(Review review) {
        Map<String, Object> row = new HashMap<>();
        row.put("film_id", review.getFilmId());
        row.put("user_id", review.getUserId());
        row.put("content", review.getContent());
        row.put("is_positive", review.isPositive());
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("reviews")
                .usingGeneratedKeyColumns("id");
        final long id = simpleJdbcInsert.executeAndReturnKey(row).longValue();
        return getReviewById(id)
                .orElseThrow(() -> new IllegalStateException("Ошибка при добавлении записи в БД"));
    }

    @Override
    public Optional<Review> updateReview(Review reviewUpdates) {
        String sql = "update reviews set content=?, is_positive=? where id=?";
        jdbcTemplate.update(sql,
                reviewUpdates.getContent(),
                reviewUpdates.isPositive(),
                reviewUpdates.getId());
        return getReviewById(reviewUpdates.getId());
    }

    @Override
    public boolean deleteReviewById(long id) {
        String sql = "delete from reviews where id=?";
        return jdbcTemplate.update(sql, id) == 1;
    }

    @Override
    public Optional<Review> getReviewById(long id) {
        String sql = "select r.id as review_id, r.film_id as film_id, r.user_id as author_id, " +
                "r.content as content, r.is_positive as is_positive, " +
                "sum(reaction.reaction) as useful " +
                "from reviews as r " +
                "left join review_reactions as reaction on r.id=reaction.review_id " +
                "where r.id=? " +
                "group by review_id, film_id, author_id, content, is_positive";

        List<Review> queryResult = jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs), id);
        if (queryResult.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(queryResult.get(0));
    }

    @Override
    public int createReviewLike(long id, long userId) {
        Map<String, Object> row = new HashMap<>();
        row.put("review_id", id);
        row.put("user_id", userId);
        row.put("reaction", 1);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("review_reactions");
        try {
            return simpleJdbcInsert.execute(row);
        } catch (DuplicateKeyException ex) {
            String sql = "update review_reactions set reaction=1 where review_id=? and user_id=?";
            return jdbcTemplate.update(sql, id, userId);
        }
    }

    @Override
    public int createReviewDislike(long id, long userId) {
        Map<String, Object> row = new HashMap<>();
        row.put("review_id", id);
        row.put("user_id", userId);
        row.put("reaction", -1);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("review_reactions");
        try {
            return simpleJdbcInsert.execute(row);
        } catch (DuplicateKeyException ex) {
            String sql = "update review_reactions set reaction=-1 where review_id=? and user_id=?";
            return jdbcTemplate.update(sql, id, userId);
        }
    }

    @Override
    public boolean deleteLikeReview(long id, long userId) {
        String sql = "delete from review_reactions where review_id=? and user_id=? and reaction=1";
        return jdbcTemplate.update(sql, id, userId) == 1;
    }

    @Override
    public boolean deleteDisLikeReview(long id, long userId) {
        String sql = "delete from review_reactions where review_id=? and user_id=? and reaction=-1";
        return jdbcTemplate.update(sql, id, userId) == 1;
    }
}
