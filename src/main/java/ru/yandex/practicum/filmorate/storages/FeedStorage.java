package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.Event;
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.Operation;
import ru.yandex.practicum.filmorate.models.User;

import java.util.List;

public interface FeedStorage {
    List<Event> getFeedByUser(User user);

    void addEvent(Long userId, Long entityId, EventType et, Operation op);
}
