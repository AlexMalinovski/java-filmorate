package ru.yandex.practicum.filmorate.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.configs.TestAppConfig;
import ru.yandex.practicum.filmorate.dto.CreatedReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdatedReviewDto;
import ru.yandex.practicum.filmorate.models.Review;
import ru.yandex.practicum.filmorate.services.ReviewService;
import ru.yandex.practicum.filmorate.services.UserService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@Import(TestAppConfig.class)
class ReviewControllerTest {

    @MockBean
    private ReviewService reviewService;

    @MockBean
    private UserService userService;

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ReviewDto getValidReviewDto() {
        return ReviewDto.builder()
                .filmId(1L)
                .userId(1L)
                .content("content")
                .isPositive(true)
                .build();
    }

    private UpdatedReviewDto getValidUpdateReviewDto() {
        return UpdatedReviewDto.builder()
                .reviewId(1L)
                .content("content")
                .isPositive(true)
                .build();
    }

    private CreatedReviewDto getValidCreatedReviewDto() {
        return CreatedReviewDto.builder()
                .reviewId(1L)
                .filmId(1L)
                .userId(1L)
                .content("content")
                .isPositive(true)
                .useful(0L)
                .build();
    }

    private Review getValidReview() {
        return Review.builder()
                .id(1L)
                .filmId(1L)
                .userId(1L)
                .content("content")
                .isPositive(true)
                .useful(0L)
                .build();
    }


    @Test
    void getReviews_isAvailable() throws Exception {
        mockMvc.perform(get("/reviews"))
                .andExpect(status().isOk());
    }

    @Test
    void createReview_isAvailable() throws Exception {
        when(reviewService.createReview(any(Review.class))).thenReturn(getValidReview());
        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getValidReviewDto())))
                .andExpect(status().isOk());
    }

    @Test
    void updateCreatedReview_isAvailable() throws Exception {
        when(reviewService.updateReview(any(Review.class))).thenReturn(Optional.of(getValidReview()));
        mockMvc.perform(put("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(getValidUpdateReviewDto())))
                .andExpect(status().isOk());
    }

    @Test
    void deleteReviewById_isAvailable() throws Exception {
        when(reviewService.deleteReviewById(anyLong())).thenReturn(Optional.of(getValidReview()));
        mockMvc.perform(delete("/reviews/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getReviewById_isAvailable() throws Exception {
        when(reviewService.getReviewById(anyLong())).thenReturn(Optional.of(getValidReview()));
        mockMvc.perform(get("/reviews/1"))
                .andExpect(status().isOk());
    }

    @Test
    void addOrUpdateLikeReview_isAvailable() throws Exception {
        when(reviewService.addOrUpdateLikeReview(anyLong(), anyLong())).thenReturn(getValidReview());
        mockMvc.perform(put("/reviews/1/like/1"))
                .andExpect(status().isOk());
    }

    @Test
    void addOrUpdateDislikeReview_isAvailable() throws Exception {
        when(reviewService.addOrUpdateDislikeReview(anyLong(), anyLong())).thenReturn(getValidReview());
        mockMvc.perform(put("/reviews/1/dislike/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteLikeReview_isAvailable() throws Exception {
        when(reviewService.deleteLikeReview(anyLong(), anyLong())).thenReturn(getValidReview());
        mockMvc.perform(delete("/reviews/1/like/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteDislikeReview_isAvailable() throws Exception {
        when(reviewService.deleteDislikeReview(anyLong(), anyLong())).thenReturn(getValidReview());
        mockMvc.perform(delete("/reviews/1/dislike/1"))
                .andExpect(status().isOk());
    }
}