package ru.yandex.practicum.filmorate.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.models.Review;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storages.ReviewStorage;
import ru.yandex.practicum.filmorate.storages.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewStorage reviewStorage;

    @Mock
    private UserStorage userStorage;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void getReviews_ifNotFound_thenReturnEmptyList() {
        when(reviewStorage.getReviews(anyLong(), anyInt())).thenReturn(new ArrayList<>());

        var actual = reviewService.getReviews(1000L, 10);

        verify(reviewStorage).getReviews(1000L, 10);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void getReviews_ifFounded_thenReturnFounded() {
        var expected = Review.builder().id(1L).build();
        when(reviewStorage.getReviews(anyLong(), anyInt())).thenReturn(List.of(expected));

        var actual = reviewService.getReviews(1L, 10);

        verify(reviewStorage).getReviews(1L, 10);
        assertNotNull(actual);
        assertEquals(1, actual.size());
        assertSame(expected, actual.get(0));
    }


    @Test
    void createReview_ifCreate_thenReturnCreated() {
        var expected = Review.builder().id(1L).build();
        when(reviewStorage.createReview(expected)).thenReturn(expected);

        var actual = reviewService.createReview(expected);

        verify(reviewStorage).createReview(expected);
        assertSame(expected, actual);
    }

    @Test
    void updateReview_ifNotFound_thenReturnEmptyOptional() {
        var expected = Review.builder().id(1L).build();
        when(reviewStorage.updateReview(expected)).thenReturn(Optional.empty());

        var actual = reviewService.updateReview(expected);

        verify(reviewStorage).updateReview(expected);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void updateReview_ifFounded_thenReturnUpdatedOptional() {
        var expected = Review.builder().id(1L).build();
        when(reviewStorage.updateReview(expected)).thenReturn(Optional.of(expected));

        var actual = reviewService.updateReview(expected);

        verify(reviewStorage).updateReview(expected);
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertSame(expected, actual.get());
    }

    @Test
    void deleteReviewById_ifNotFound_thenReturnEmptyOptional() {
        when(reviewStorage.getReviewById(anyLong())).thenReturn(Optional.empty());

        var actual = reviewService.deleteReviewById(1L);

        verify(reviewStorage).getReviewById(1L);
        verify(reviewStorage, never()).deleteReviewById(1L);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void deleteReviewById_ifFounded_thenReturnDeletedOptional() {
        var expected = Review.builder().id(1L).build();
        when(reviewStorage.getReviewById(anyLong())).thenReturn(Optional.of(expected));
        when(reviewStorage.deleteReviewById(1L)).thenReturn(true);

        var actual = reviewService.deleteReviewById(1L);

        verify(reviewStorage).getReviewById(1L);
        verify(reviewStorage).deleteReviewById(1L);
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertSame(expected, actual.get());
    }

    @Test
    void getReviewById_ifNotFound_thenReturnEmptyOptional() {
        when(reviewStorage.getReviewById(1L)).thenReturn(Optional.empty());

        var actual = reviewService.getReviewById(1L);

        verify(reviewStorage).getReviewById(1L);
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    void getReviewById_ifFounded_thenReturnFoundedOptional() {
        var expected = Review.builder().id(1L).build();
        when(reviewStorage.getReviewById(1L)).thenReturn(Optional.of(expected));

        var actual = reviewService.getReviewById(1L);

        verify(reviewStorage).getReviewById(1L);
        assertNotNull(actual);
        assertTrue(actual.isPresent());
        assertSame(expected, actual.get());
    }

    @Test
    void addOrUpdateLikeReview_ifUserNotFound_thenThrowNotFoundException() {
        var expected = Review.builder().id(1L).build();
        when(userStorage.getUserById(anyLong())).thenThrow(new NotFoundException("not found"));
        when(reviewStorage.getReviewById(anyLong())).thenReturn(Optional.of(expected));

        assertThrows(NotFoundException.class, () -> reviewService.addOrUpdateLikeReview(1L, 1L));
    }

    @Test
    void addOrUpdateLikeReview_ifReviewNotFound_thenThrowNotFoundException() {
        when(reviewStorage.getReviewById(anyLong())).thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> reviewService.addOrUpdateLikeReview(1L, 1L));
    }

    @Test
    void addOrUpdateLikeReview_ifAdded_thenReturnAdded() {
        var expected = Review.builder().id(1L).build();
        when(userStorage.getUserById(anyLong())).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(reviewStorage.getReviewById(anyLong())).thenReturn(Optional.of(expected));
        when(reviewStorage.createReviewLike(anyLong(), anyLong())).thenReturn(1);

        var actual = reviewService.addOrUpdateLikeReview(1L, 1L);

        verify(userStorage).getUserById(1L);
        verify(reviewStorage, times(2)).getReviewById(1L);
        verify(reviewStorage).createReviewLike(1L, 1L);
        assertSame(expected, actual);
    }

    @Test
    void addOrUpdateDislikeReview_ifUserNotFound_thenThrowNotFoundException() {
        var expected = Review.builder().id(1L).build();
        when(userStorage.getUserById(anyLong())).thenThrow(new NotFoundException("not found"));
        when(reviewStorage.getReviewById(anyLong())).thenReturn(Optional.of(expected));

        assertThrows(NotFoundException.class, () -> reviewService.addOrUpdateDislikeReview(1L, 1L));
    }

    @Test
    void addOrUpdateDislikeReview_ifReviewNotFound_thenThrowNotFoundException() {
        when(reviewStorage.getReviewById(anyLong())).thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> reviewService.addOrUpdateDislikeReview(1L, 1L));
    }

    @Test
    void addOrUpdateDislikeReview_ifAdded_thenReturnAdded() {
        var expected = Review.builder().id(1L).build();
        when(userStorage.getUserById(anyLong())).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(reviewStorage.getReviewById(anyLong())).thenReturn(Optional.of(expected));
        when(reviewStorage.createReviewDislike(anyLong(), anyLong())).thenReturn(1);

        var actual = reviewService.addOrUpdateDislikeReview(1L, 1L);

        verify(userStorage).getUserById(1L);
        verify(reviewStorage, times(2)).getReviewById(1L);
        verify(reviewStorage).createReviewDislike(1L, 1L);
        assertSame(expected, actual);
    }

    @Test
    void deleteLikeReview_ifUserNotFound_thenThrowNotFoundException() {
        var expected = Review.builder().id(1L).build();
        when(userStorage.getUserById(anyLong())).thenThrow(new NotFoundException("not found"));
        when(reviewStorage.getReviewById(anyLong())).thenReturn(Optional.of(expected));

        assertThrows(NotFoundException.class, () -> reviewService.deleteLikeReview(1L, 1L));
    }

    @Test
    void deleteLikeReview_ifLikeNotFound_thenThrowNotFoundException() {
        var expected = Review.builder().id(1L).build();
        when(userStorage.getUserById(anyLong())).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(reviewStorage.getReviewById(anyLong())).thenReturn(Optional.of(expected));
        when(reviewStorage.deleteLikeReview(anyLong(), anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> reviewService.deleteLikeReview(1L, 1L));
        verify(userStorage).getUserById(1L);
        verify(reviewStorage).getReviewById(1L);
        verify(reviewStorage).deleteLikeReview(1L, 1L);
    }


    @Test
    void deleteLikeReview_ifReviewNotFound_thenThrowNotFoundException() {
        when(reviewStorage.getReviewById(anyLong())).thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> reviewService.deleteLikeReview(1L, 1L));
    }

    @Test
    void deleteLikeReview_ifDelete_thenReturnDeleted() {
        var expected = Review.builder().id(1L).build();
        when(userStorage.getUserById(anyLong())).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(reviewStorage.getReviewById(anyLong())).thenReturn(Optional.of(expected));
        when(reviewStorage.deleteLikeReview(anyLong(), anyLong())).thenReturn(true);

        var actual = reviewService.deleteLikeReview(1L, 1L);

        verify(userStorage).getUserById(1L);
        verify(reviewStorage, times(2)).getReviewById(1L);
        verify(reviewStorage).deleteLikeReview(1L, 1L);
        assertSame(expected, actual);
    }

    @Test
    void deleteDislikeReview_ifUserNotFound_thenThrowNotFoundException() {
        var expected = Review.builder().id(1L).build();
        when(userStorage.getUserById(anyLong())).thenThrow(new NotFoundException("not found"));
        when(reviewStorage.getReviewById(anyLong())).thenReturn(Optional.of(expected));

        assertThrows(NotFoundException.class, () -> reviewService.deleteDislikeReview(1L, 1L));
    }

    @Test
    void deleteDislikeReview_ifReviewNotFound_thenThrowNotFoundException() {
        when(reviewStorage.getReviewById(anyLong())).thenThrow(new NotFoundException("not found"));

        assertThrows(NotFoundException.class, () -> reviewService.deleteDislikeReview(1L, 1L));
    }

    @Test
    void deleteDislikeReview_ifLikeNotFound_thenThrowNotFoundException() {
        var expected = Review.builder().id(1L).build();
        when(userStorage.getUserById(anyLong())).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(reviewStorage.getReviewById(anyLong())).thenReturn(Optional.of(expected));
        when(reviewStorage.deleteDisLikeReview(anyLong(), anyLong())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> reviewService.deleteDislikeReview(1L, 1L));
        verify(userStorage).getUserById(1L);
        verify(reviewStorage).getReviewById(1L);
        verify(reviewStorage).deleteDisLikeReview(1L, 1L);
    }

    @Test
    void deleteDislikeReview_ifDelete_thenReturnDeleted() {
        var expected = Review.builder().id(1L).build();
        when(userStorage.getUserById(anyLong())).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(reviewStorage.getReviewById(anyLong())).thenReturn(Optional.of(expected));
        when(reviewStorage.deleteDisLikeReview(anyLong(), anyLong())).thenReturn(true);

        var actual = reviewService.deleteDislikeReview(1L, 1L);

        verify(userStorage).getUserById(1L);
        verify(reviewStorage, times(2)).getReviewById(1L);
        verify(reviewStorage).deleteDisLikeReview(1L, 1L);
        assertSame(expected, actual);
    }
}