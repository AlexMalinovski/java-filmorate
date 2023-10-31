package ru.yandex.practicum.filmorate.storages;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.models.Review;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DbReviewStorageTest {

    @Autowired
    private ReviewStorage reviewStorage;

    @Test
    @Sql({"/test-reviews.sql"})
    void getReviews_ifNotFoundId_thenReturnEmptyList() {
        var actual = reviewStorage.getReviews(1000L, 10);

        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

    @Test
    @Sql({"/test-reviews.sql"})
    void getReviews_ifIdNotNullAndFoundId_thenReturnWithLimit() {
        var actual = reviewStorage.getReviews(1L, 1);

        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertEquals(1L, actual.get(0).getFilmId());
    }

    @Test
    @Sql({"/test-reviews.sql"})
    void getReviews_ifIdIsNull_thenReturnWithLimit() {
        var actual = reviewStorage.getReviews(null, 2);

        assertNotNull(actual);
        assertEquals(2, actual.size());
    }

    @Test
    @Sql({"/test-reviews-create.sql"})
    void createReview() {
        final Review expected = Review.builder()
                .filmId(1L)
                .userId(1L)
                .content("test")
                .isPositive(true)
                .build();

        var actual = Optional.of(reviewStorage.createReview(expected));

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("userId", expected.getUserId());
                    assertThat(obj).hasFieldOrPropertyWithValue("filmId", expected.getFilmId());
                    assertThat(obj).hasFieldOrPropertyWithValue("content", expected.getContent());
                    assertThat(obj).hasFieldOrPropertyWithValue("isPositive", expected.isPositive());
                    assertThat(obj).hasFieldOrPropertyWithValue("useful", 0L);
                });
    }

    @Test
    @Sql({"/data-clear.sql"})
    void updateReview_ifNotFound_thenReturnEmptyOptional() {
        var actual = reviewStorage.updateReview(Review
                .builder()
                .id(99999L)
                .filmId(1L)
                .userId(1L)
                .content("new content")
                .isPositive(true)
                .build());

        assertThat(actual)
                .isEmpty();

    }

    @Test
    @Sql({"/test-reviews.sql"})
    void updateReview_ifFounded_thenReturnUpdated() {
        final Review expected = Review.builder()
                .id(1L)
                .filmId(1L)
                .userId(1L)
                .content("new content")
                .isPositive(true)
                .build();

        var actual = reviewStorage.updateReview(expected);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", expected.getId());
                    assertThat(obj).hasFieldOrPropertyWithValue("content", expected.getContent());
                });
    }

    @Test
    @Sql({"/test-reviews.sql"})
    void deleteReviewById_ifFounded_thenTrue() {
        var actual = reviewStorage.deleteReviewById(1L);
        assertTrue(actual);
    }

    @Test
    @Sql({"/test-reviews.sql"})
    void deleteReviewById_ifNotFound_thenFalse() {
        var actual = reviewStorage.deleteReviewById(1000L);
        assertFalse(actual);
    }

    @Test
    @Sql({"/test-reviews.sql"})
    void getReviewById_ifNotFound_thenReturnEmptyOptional() {
        var actual = reviewStorage.getReviewById(10000L);

        assertThat(actual)
                .isEmpty();
    }

    @Test
    @Sql({"/test-reviews.sql"})
    void getReviewById_ifHasReactions_thenReturnReviewWithNotZeroUseful() {
        var actual = reviewStorage.getReviewById(1L);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("userId", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("filmId", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("content", "not bad by user 1");
                    assertThat(obj).hasFieldOrPropertyWithValue("isPositive", true);
                    assertThat(obj).hasFieldOrPropertyWithValue("useful", 1L);
                });
    }

    @Test
    @Sql({"/test-reviews.sql"})
    void getReviewById_ifWithoutReactions_ReturnReviewWithZeroUseful() {
        var actual = reviewStorage.getReviewById(2L);

        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(obj -> {
                    assertThat(obj).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(obj).hasFieldOrPropertyWithValue("userId", 2L);
                    assertThat(obj).hasFieldOrPropertyWithValue("filmId", 1L);
                    assertThat(obj).hasFieldOrPropertyWithValue("content", "not bad by user 2");
                    assertThat(obj).hasFieldOrPropertyWithValue("isPositive", true);
                    assertThat(obj).hasFieldOrPropertyWithValue("useful", 0L);
                });
    }

    @Test
    @Sql({"/test-reviews.sql"})
    void createReviewLike() {
        int actual = reviewStorage.createReviewLike(3L, 1L);
        assertEquals(1, actual);

        actual = reviewStorage.createReviewLike(3L, 1L);
        assertEquals(1, actual);
    }

    @Test
    @Sql({"/test-reviews.sql"})
    void createReviewDislike() {
        int actual = reviewStorage.createReviewDislike(3L, 1L);
        assertEquals(1, actual);

        actual = reviewStorage.createReviewDislike(3L, 1L);
        assertEquals(1, actual);
    }

    @Test
    @Sql({"/test-reviews.sql"})
    void deleteLikeReview_ifDeleted_thenTrue() {
        boolean actual = reviewStorage.deleteLikeReview(1L, 2L);
        assertTrue(actual);
    }

    @Test
    @Sql({"/test-reviews.sql"})
    void deleteDisLikeReview_ifDeleted_thenTrue() {
        boolean actual = reviewStorage.deleteDisLikeReview(1L, 3L);
        assertTrue(actual);
    }

    @Test
    @Sql({"/test-reviews.sql"})
    void deleteLikeReview_ifNotFound_thenFalse() {
        boolean actual = reviewStorage.deleteLikeReview(100L, 2L);
        assertFalse(actual);
    }

    @Test
    @Sql({"/test-reviews.sql"})
    void deleteDisLikeReview_ifNotFound_thenFalse() {
        boolean actual = reviewStorage.deleteDisLikeReview(100L, 3L);
        assertFalse(actual);
    }
}