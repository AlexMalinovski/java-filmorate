package ru.yandex.practicum.filmorate.services;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.models.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    @NonNull
    List<Review> getReviews(@Nullable Long filmId, int count);

    @NonNull
    Review createReview(@NonNull Review review);

    @NonNull
    Optional<Review> updateReview(@NonNull Review reviewUpdates);

    @NonNull
    Optional<Review> deleteReviewById(long id);

    Optional<Review> getReviewById(long id);

    @NonNull
    Review addOrUpdateLikeReview(long id, long userId);

    @NonNull
    Review addOrUpdateDislikeReview(long id, long userId);

    @NonNull
    Review deleteLikeReview(long id, long userId);

    @NonNull
    Review deleteDislikeReview(long id, long userId);
}
