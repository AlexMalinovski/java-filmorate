package ru.yandex.practicum.filmorate.converters;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import ru.yandex.practicum.filmorate.configs.TestAppConfig;
import ru.yandex.practicum.filmorate.dto.CreatedReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdatedReviewDto;
import ru.yandex.practicum.filmorate.models.Review;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Import(TestAppConfig.class)
class ReviewGenericConverterTest {

    @Autowired
    private ConversionService conversionService;

    @Test
    public void updateReviewDto_to_Review_isConvertible() {
        UpdatedReviewDto dto = UpdatedReviewDto.builder()
                .reviewId(1L)
                .content("content")
                .isPositive(true)
                .build();
        Review review = conversionService.convert(dto, Review.class);
        assertNotNull(review);
        assertEquals(dto.getReviewId(), (long) review.getId());
        assertEquals(dto.getContent(), review.getContent());
        assertEquals(dto.getIsPositive(), review.isPositive());
    }

    @Test
    public void review_to_createdReviewDto_isConvertible() {
        Review review = Review.builder()
                .id(1L)
                .userId(1L)
                .filmId(1L)
                .content("content")
                .isPositive(true)
                .useful(10L)
                .build();
        CreatedReviewDto dto = conversionService.convert(review, CreatedReviewDto.class);
        assertNotNull(dto);
        assertEquals(review.getId(), dto.getReviewId());
        assertEquals(review.getUserId(), dto.getUserId());
        assertEquals(review.getFilmId(), dto.getFilmId());
        assertEquals(review.getContent(), dto.getContent());
        assertEquals(review.isPositive(), dto.isPositive());
        assertEquals(review.getUseful(), dto.getUseful());
    }

    @Test
    public void reviewDto_to_Review_isConvertible() {
        ReviewDto dto = ReviewDto.builder()
                .userId(1L)
                .filmId(1L)
                .content("content")
                .isPositive(true)
                .build();
        Review review = conversionService.convert(dto, Review.class);
        assertNotNull(review);
        assertNull(review.getId());
        assertEquals(dto.getFilmId(), review.getFilmId());
        assertEquals(dto.getUserId(), review.getUserId());
        assertEquals(dto.getContent(), review.getContent());
        assertEquals(dto.getIsPositive(), review.isPositive());
    }
}