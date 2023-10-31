package ru.yandex.practicum.filmorate.storages;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.Operation;
import ru.yandex.practicum.filmorate.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DbFeedStorageTest {

    @Autowired
    @Qualifier("dbFeedStorage")
    private FeedStorage feedStorage;

    @Test
    @Sql({"/test-feed.sql"})
    void getUserFeed() {
        var user = User.builder().id(1L).login("nameOne").name("nameOne").build();
        List<Long> userIds = new ArrayList<>(user.getFriends());
        userIds.add(user.getId());
        Map<String, Object> params = new HashMap<>();
        params.put("userIds", userIds);
        params.put("userId", user.getId());
        params.put("likeType", EventType.LIKE.name());
        params.put("reviewType", EventType.REVIEW.name());
        var actual = feedStorage.getUserFeed(params);

        Assertions.assertThat(actual).hasSize(3);
        Assertions.assertThat(actual.get(0)).hasFieldOrPropertyWithValue("eventId", 1L);
        Assertions.assertThat(actual.get(0)).hasFieldOrPropertyWithValue("userId", 1L);
    }

    @Test
    @Sql({"/test-feed.sql"})
    void addEvent() {
        Event event = Event.builder()
                .userId(1L)
                .entityId(2L)
                .eventType(EventType.REVIEW)
                .operation(Operation.ADD)
                .timestamp(1698585287841L)
                .build();
        Optional<Event> savedEventOpt = Optional.ofNullable(feedStorage.addEvent(event));
        assertThat(savedEventOpt)
                .isPresent()
                .hasValueSatisfying(savedEvent -> {
                    assertThat(savedEvent).hasFieldOrPropertyWithValue("eventId", 5L);
                    assertThat(savedEvent).hasFieldOrPropertyWithValue("userId", event.getUserId());
                    assertThat(savedEvent).hasFieldOrPropertyWithValue("entityId", event.getEntityId());
                    assertThat(savedEvent).hasFieldOrPropertyWithValue("eventType", event.getEventType());
                });
    }
}