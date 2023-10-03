delete from user_friends;
delete from film_likes;
delete from film_genres;
delete from users;
delete from films;

insert into films (id, name, description, release_date, duration, rating)
values (1, 'film1', 'descr1', '2000-01-01', 120, 'PG'),
    (2, 'film2', 'descr2', '2010-01-01', 130, 'PG'),
    (3, 'film3', 'descr3', '2015-01-01', 140, 'PG')
on conflict do nothing;

insert into users (id, email, login, name, birthday)
values (1, 'user1@mail.ru', 'user1', 'user1', '1950-01-01'),
    (2, 'user2@mail.ru', 'user2', 'user2', '1980-01-01'),
    (3, 'user3@mail.ru', 'user3', 'user3', '1990-01-01')
on conflict do nothing;

insert into film_genres (film_id, genre_id)
values (1, 1), (2, 2), (2, 3)
on conflict do nothing;

insert into film_likes (film_id, user_id)
values (3, 1), (3, 2), (3, 3), (2, 1), (2, 2), (1, 1)
on conflict do nothing;

insert into user_friends (user_id, friend_id)
values (1, 2), (1, 3), (2, 1), (2, 3)
on conflict do nothing;