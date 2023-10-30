package ru.yandex.practicum.filmorate.storages;

import ru.yandex.practicum.filmorate.models.Event;

import java.util.List;
import java.util.Map;

public interface FeedStorage {
    List<Event> getUserFeed(Map<String, Object> params);

    Event addEvent(Event event);
}
