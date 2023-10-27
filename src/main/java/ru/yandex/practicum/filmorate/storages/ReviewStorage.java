package ru.yandex.practicum.filmorate.storages;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.models.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    List<Review> getReviews(@Nullable Long filmId, int count);

    Review createReview(@NonNull Review review);

    Optional<Review> updateReview(@NonNull Review reviewUpdates);

    boolean deleteReviewById(long id);

    Optional<Review> getReviewById(long id);

    int createReviewLike(long id, long userId);

    int createReviewDislike(long id, long userId);

    boolean deleteLikeReview(long id, long userId);

    boolean deleteDisLikeReview(long id, long userId);
}
