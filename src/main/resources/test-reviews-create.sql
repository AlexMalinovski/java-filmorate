delete from users;
delete from films;
delete from reviews;
delete from review_reactions;

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
