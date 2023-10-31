delete from user_friends;
delete from film_likes;
delete from film_genres;
delete from users;
delete from films;
delete from feed;
delete from reviews;
delete from review_reactions;
delete from film_directors;

alter table feed alter column event_id restart with 1;
alter table films alter column id restart with 1;
alter table users alter column id restart with 1;
alter table directors alter column id restart with 1;
alter table reviews alter column id restart with 1;

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

insert into feed (user_id, entity_id, event_type, operation, timestamp)
values (1, 2, 'FRIEND', 'ADD', 1698599670000),
       (1, 3, 'FRIEND', 'ADD', 1698599671000),
       (2, 3, 'FRIEND', 'ADD', 1698599672000),
       (1, 1, 'LIKE', 'ADD', 1698599673000)
on conflict do nothing;


