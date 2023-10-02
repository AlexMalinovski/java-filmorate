DELETE FROM user_friends;
DELETE FROM film_likes;
DELETE FROM film_genres;
DELETE FROM users;
DELETE FROM films;

INSERT INTO films (id, name, description, release_date, duration, rating)
VALUES (1, 'film1', 'descr1', '2000-01-01', 120, 'PG'),
    (2, 'film2', 'descr2', '2010-01-01', 130, 'PG'),
    (3, 'film3', 'descr3', '2015-01-01', 140, 'PG')
ON CONFLICT DO NOTHING;

INSERT INTO users (id, email, login, name, birthday)
VALUES (1, 'user1@mail.ru', 'user1', 'user1', '1950-01-01'),
    (2, 'user2@mail.ru', 'user2', 'user2', '1980-01-01'),
    (3, 'user3@mail.ru', 'user3', 'user3', '1990-01-01')
ON CONFLICT DO NOTHING;

INSERT INTO film_genres (film_id, genre_id)
VALUES (1, 1), (2, 2), (2, 3)
ON CONFLICT DO NOTHING;

INSERT INTO film_likes (film_id, user_id)
VALUES (3, 1), (3, 2), (3, 3), (2, 1), (2, 2), (1, 1)
ON CONFLICT DO NOTHING;

INSERT INTO user_friends (user_id, friend_id)
VALUES (1, 2), (1, 3), (2, 1), (2, 3)
ON CONFLICT DO NOTHING;