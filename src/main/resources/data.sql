insert into genres (id, name)
values (1, 'Комедия'), (2,'Драма'), (3,'Мультфильм'), (4,'Триллер'), (5,'Документальный'), (6,'Боевик')
on conflict do nothing;
