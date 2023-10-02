package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    private Long id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    @Builder.Default
    private Set<Long> friends = new HashSet<>();

    public User copyOf() {
        return User.builder()
                .id(this.id)
                .email(this.email)
                .login(this.login)
                .name(this.name)
                .birthday(this.birthday)
                .friends(new HashSet<>(friends))
                .build();
    }

    public boolean addFriend(long userId) {
        return friends.add(userId);
    }

    public void addFriend(Set<Long> usersId) {
        this.friends.addAll(usersId);
    }

    public boolean removeFriend(long userId) {
        return friends.remove(userId);
    }

    public int getNumOfFriends() {
        return friends.size();
    }
}
