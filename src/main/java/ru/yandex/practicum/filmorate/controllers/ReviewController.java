package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.CreatedReviewDto;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.dto.UpdatedReviewDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Review;
import ru.yandex.practicum.filmorate.services.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final ConversionService conversionService;


    @GetMapping
    public ResponseEntity<List<CreatedReviewDto>> getReviews(@RequestParam(required = false) Long filmId,
                                                             @RequestParam(defaultValue = "10") int count) {
        List<CreatedReviewDto> reviewsDto = reviewService.getReviews(filmId, count)
                .stream()
                .map(r -> conversionService.convert(r, CreatedReviewDto.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(reviewsDto);
    }

    /**
     * Добавление нового отзыва.
     * @param reviewDto ReviewDto
     * @return CreatedReviewDto
     */
    @PostMapping
    public ResponseEntity<CreatedReviewDto> createReview(@Valid @RequestBody ReviewDto reviewDto) {
        if (reviewDto.getUserId() <= 0 || reviewDto.getFilmId() <= 0) {
            throw new NotFoundException("Переданы некорректные идентификаторы пользователя или фильма");
        }
        Review review = Optional.ofNullable(conversionService.convert(reviewDto, Review.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации ReviewDto->Review. Метод вернул null."));
        Review createdReview = reviewService.createReview(review);
        log.debug("Добавлен новый отзыв на фильм с id={}", createdReview.getFilmId());
        return ResponseEntity.ok(conversionService.convert(createdReview, CreatedReviewDto.class));
    }

    /**
     * Редактирование уже имеющегося отзыва.
     * @param updatedReviewDto UpdatedReviewDto
     * @return CreatedReviewDto
     */
    @PutMapping
    public ResponseEntity<CreatedReviewDto> updateCreatedReview(@Valid @RequestBody UpdatedReviewDto updatedReviewDto) {
        final Review reviewUpdates = Optional.ofNullable(conversionService.convert(updatedReviewDto, Review.class))
                .orElseThrow(() -> new IllegalStateException("Ошибка конвертации UpdatedReviewDto->Review. Метод вернул null."));
        final Review result = reviewService.updateReview(reviewUpdates)
                .orElseThrow(() -> new NotFoundException("Отзыв на фильм не найден"));
        log.debug("Изменён отзыв с id={}", reviewUpdates.getId());
        return ResponseEntity.ok(conversionService.convert(result, CreatedReviewDto.class));
    }

    /**
     * Удаление уже имеющегося отзыва.
     * @param id long ID
     * @return CreatedReviewDto
     */
    @DeleteMapping(path = "/{id}")
    public ResponseEntity<CreatedReviewDto> deleteReviewById(@PathVariable final long id) {
        if (id <= 0) {
            throw new NotFoundException("Некорректные параметры URL");
        }
        final Review result = reviewService.deleteReviewById(id)
                .orElseThrow(() -> new NotFoundException("Отзыв не найден"));
        log.debug("Удалён отзыв с id={}", id);
        return ResponseEntity.ok(conversionService.convert(result, CreatedReviewDto.class));
    }

    /**
     * Получение отзыва по идентификатору.
     * @param id Идентификатор отзыва
     * @return CreatedReviewDto
     */
    @GetMapping(path = "/{id}")
    public ResponseEntity<CreatedReviewDto> getReviewById(@PathVariable long id) {
        if (id <= 0) {
            throw new NotFoundException("Некорректные параметры URL");
        }
        return reviewService.getReviewById(id)
                .map(r -> conversionService.convert(r, CreatedReviewDto.class))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new NotFoundException("Не найден отзыв с id:" + id));
    }

    /**
     * пользователь ставит лайк отзыву.
     * Существующая реакция пользователя на отзыв будет перезаписана.
     * @param id id отзыва
     * @param userId id пользователя
     * @return CreatedReviewDto
     */
    @PutMapping(path = "/{id}/like/{userId}")
    public ResponseEntity<CreatedReviewDto> addOrUpdateLikeReview(@PathVariable long id,
                                                   @PathVariable long userId) {
        if (id <= 0 || userId <= 0) {
            throw new NotFoundException("Некорректные параметры URL");
        }
        Review likedReview = reviewService.addOrUpdateLikeReview(id, userId);
        log.debug("Пользователь id={} лайкнул отзыв id={}", userId, id);
        return ResponseEntity.ok(conversionService.convert(likedReview, CreatedReviewDto.class));
    }

    /**
     * пользователь ставит дизлайк отзыву.
     * Существующая реакция пользователя на отзыв будет перезаписана.
     * @param id id отзыва
     * @param userId id пользователя
     * @return CreatedReviewDto
     */
    @PutMapping(path = "/{id}/dislike/{userId}")
    public ResponseEntity<CreatedReviewDto> addOrUpdateDislikeReview(@PathVariable long id,
                                                       @PathVariable long userId) {
        if (id <= 0 || userId <= 0) {
            throw new NotFoundException("Некорректные параметры URL");
        }
        Review likedReview = reviewService.addOrUpdateDislikeReview(id, userId);
        log.debug("Пользователь id={} дизлайкнул отзыв id={}", userId, id);
        return ResponseEntity.ok(conversionService.convert(likedReview, CreatedReviewDto.class));
    }

    /**
     * пользователь ставит лайк отзыву.
     * Существующая реакция пользователя на отзыв будет перезаписана.
     * @param id id отзыва
     * @param userId id пользователя
     * @return CreatedReviewDto
     */
    @DeleteMapping(path = "/{id}/like/{userId}")
    public ResponseEntity<CreatedReviewDto> deleteLikeReview(@PathVariable long id,
                                                       @PathVariable long userId) {
        if (id <= 0 || userId <= 0) {
            throw new NotFoundException("Некорректные параметры URL");
        }
        Review likedReview = reviewService.deleteLikeReview(id, userId);
        log.debug("Пользователь id={} удалил лайк отзыва id={}", userId, id);
        return ResponseEntity.ok(conversionService.convert(likedReview, CreatedReviewDto.class));
    }

    /**
     * пользователь ставит дизлайк отзыву.
     * Существующая реакция пользователя на отзыв будет перезаписана.
     * @param id id отзыва
     * @param userId id пользователя
     * @return CreatedReviewDto
     */
    @DeleteMapping(path = "/{id}/dislike/{userId}")
    public ResponseEntity<CreatedReviewDto> deleteDislikeReview(@PathVariable long id,
                                                          @PathVariable long userId) {
        if (id <= 0 || userId <= 0) {
            throw new NotFoundException("Некорректные параметры URL");
        }
        Review likedReview = reviewService.deleteDislikeReview(id, userId);
        log.debug("Пользователь id={} удалил дизлайк отзыва id={}", userId, id);
        return ResponseEntity.ok(conversionService.convert(likedReview, CreatedReviewDto.class));
    }
}