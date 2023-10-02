package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    private FilmRating rating;

    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    public Film copyOf() {
        return Film.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .releaseDate(this.releaseDate)
                .duration(this.duration)
                .likes(new HashSet<>(this.likes))
                .genres(new HashSet<>(this.genres))
                .rating(this.rating)
                .build();
    }

    public boolean addLike(Long userId) {
        return likes.add(userId);
    }

    public void addLike(Set<Long> likes) {
        this.likes.addAll(likes);
    }

    public boolean removeLike(Long userId) {
        return likes.remove(userId);
    }

    public int getNumOfLikes() {
        return likes.size();
    }

    public void addGenre(Set<Genre> genres) {
        this.genres.addAll(genres);
    }

    public void addGenre(Genre genre) {
        this.genres.add(genre);
    }
}
