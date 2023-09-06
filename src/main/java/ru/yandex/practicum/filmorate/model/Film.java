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

    public Film copyOf() {
        return Film.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .releaseDate(this.releaseDate)
                .duration(this.duration)
                .likes(new HashSet<>(this.likes))
                .build();
    }

    public boolean addLike(Long userId) {
        return likes.add(userId);
    }

    public boolean removeLike(Long userId) {
        return likes.remove(userId);
    }

    public int getNumOfLikes() {
        return likes.size();
    }
}
