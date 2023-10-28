package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Review;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.ReviewStorage;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;

    @Override
    @Transactional(readOnly = true)
    public List<Review> getReviews(Long filmId, int count) {
        return reviewStorage.getReviews(filmId, count);
    }

    @Override
    @Transactional
    public Review createReview(Review review) {
        return reviewStorage.createReview(review);
    }

    @Override
    @Transactional
    public Optional<Review> updateReview(Review reviewUpdates) {
        return reviewStorage.updateReview(reviewUpdates);
    }

    @Override
    @Transactional
    public Optional<Review> deleteReviewById(long id) {
        return reviewStorage.getReviewById(id)
                .map(founded -> {
                    reviewStorage.deleteReviewById(founded.getId());
                    return founded;
                })
                .or(Optional::empty);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Review> getReviewById(long id) {
        return reviewStorage.getReviewById(id);
    }

    @Override
    @Transactional
    public Review addOrUpdateLikeReview(long id, long userId) {
        final Review review = reviewStorage.getReviewById(id)
                .orElseThrow(() -> new NotFoundException("Не найден отзыв с id:" + id));
        final User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id:" + userId));
        reviewStorage.createReviewLike(review.getId(), user.getId());
        return reviewStorage.getReviewById(id)
                .orElseThrow(() -> new IllegalStateException("Не найден отзыв с id:" + id));
    }

    @Override
    public Review addOrUpdateDislikeReview(long id, long userId) {
        final Review review = reviewStorage.getReviewById(id)
                .orElseThrow(() -> new NotFoundException("Не найден отзыв с id:" + id));
        final User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id:" + userId));
        reviewStorage.createReviewDislike(review.getId(), user.getId());
        return reviewStorage.getReviewById(id)
                .orElseThrow(() -> new IllegalStateException("Не найден отзыв с id:" + id));
    }

    @Override
    @Transactional
    public Review deleteLikeReview(long id, long userId) {
        final Review review = reviewStorage.getReviewById(id)
                .orElseThrow(() -> new NotFoundException("Не найден отзыв с id:" + id));
        final User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id:" + userId));
        if (reviewStorage.deleteLikeReview(review.getId(), user.getId())) {
            return reviewStorage.getReviewById(id)
                    .orElseThrow(() -> new IllegalStateException("Не найден отзыв с id:" + id));
        }
        throw new NotFoundException("Не найден лайк пользователя c id:" + userId + " для отзыва с id:" + id);
    }

    @Override
    @Transactional
    public Review deleteDislikeReview(long id, long userId) {
        final Review review = reviewStorage.getReviewById(id)
                .orElseThrow(() -> new NotFoundException("Не найден отзыв с id:" + id));
        final User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Не найден пользователь с id:" + userId));
        if (reviewStorage.deleteDisLikeReview(review.getId(), user.getId())) {
            return reviewStorage.getReviewById(id)
                    .orElseThrow(() -> new IllegalStateException("Не найден отзыв с id:" + id));
        }
        throw new NotFoundException("Не найден дизлайк пользователя c id:" + userId + " для отзыва с id:" + id);
    }
}
