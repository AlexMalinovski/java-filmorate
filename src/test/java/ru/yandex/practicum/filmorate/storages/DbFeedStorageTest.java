package ru.yandex.practicum.filmorate.storages;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.Operation;
import ru.yandex.practicum.filmorate.models.User;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class DbFeedStorageTest {

    @Autowired
    @Qualifier("dbFeedStorage")
    private FeedStorage feedStorage;

    @Test
    @Sql({"/test-feed.sql"})
    void getFeedByUser() {
        var user = User.builder().id(1L).login("nameOne").name("nameOne").build();
        var actual = feedStorage.getFeedByUser(user);

        Assertions.assertThat(actual).hasSize(3);
        Assertions.assertThat(actual.get(0)).hasFieldOrPropertyWithValue("eventId", 1L);
        Assertions.assertThat(actual.get(0)).hasFieldOrPropertyWithValue("userId", 1L);
    }

    @Test
    @Sql({"/test-feed.sql"})
    void addEvent() {
        feedStorage.addEvent(1L, 2L, EventType.REVIEW, Operation.ADD);

        var user = User.builder().id(1L).login("nameOne").name("nameOne").build();
        var actual = feedStorage.getFeedByUser(user);

        Assertions.assertThat(actual).hasSize(4);
        Assertions.assertThat(actual.get(3)).hasFieldOrPropertyWithValue("eventId", 5L);
        Assertions.assertThat(actual.get(3)).hasFieldOrPropertyWithValue("userId", 1L);
        Assertions.assertThat(actual.get(3)).hasFieldOrPropertyWithValue("eventType", EventType.REVIEW);
        Assertions.assertThat(actual.get(3)).hasFieldOrPropertyWithValue("operation", Operation.ADD);
    }
}