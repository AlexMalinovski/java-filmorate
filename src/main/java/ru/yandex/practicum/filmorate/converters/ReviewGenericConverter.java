package ru.yandex.practicum.filmorate.converters;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dto.CreatedReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdatedReviewDto;
import ru.yandex.practicum.filmorate.models.Review;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
public class ReviewGenericConverter extends AbstractGenericConverter {
    @Override
    protected Map<ConvertiblePair, Function<Object, Object>> setSupportConversions() {
        return Map.ofEntries(
                new AbstractMap.SimpleEntry<>(
                        new ConvertiblePair(UpdatedReviewDto.class, Review.class),
                        this::convertUpdateReviewDtoToReview),
                new AbstractMap.SimpleEntry<>(
                        new ConvertiblePair(ReviewDto.class, Review.class),
                        this::convertReviewDtoToReview),
                new AbstractMap.SimpleEntry<>(
                        new ConvertiblePair(Review.class, CreatedReviewDto.class),
                        this::convertReviewToCreatedReviewDto));
    }

    private Review convertUpdateReviewDtoToReview(Object updateReviewDto) {
        UpdatedReviewDto dto = (UpdatedReviewDto) updateReviewDto;

        return Review.builder()
                .id(dto.getReviewId())
                .content(dto.getContent())
                .isPositive(dto.getIsPositive())
                .build();
    }

    private Review convertReviewDtoToReview(Object reviewDto) {
        ReviewDto dto = (ReviewDto) reviewDto;
        return Review.builder()
                .content(dto.getContent())
                .isPositive(dto.getIsPositive())
                .userId(dto.getUserId())
                .filmId(dto.getFilmId())
                .build();
    }

    private CreatedReviewDto convertReviewToCreatedReviewDto(Object reviewObj) {
        Review review = (Review) reviewObj;
        return CreatedReviewDto.builder()
                .reviewId(review.getId())
                .content(review.getContent())
                .isPositive(review.isPositive())
                .filmId(review.getFilmId())
                .userId(review.getUserId())
                .useful(review.getUseful())
                .build();
    }
}
