INSERT INTO genres (id, name)
VALUES (1, 'Комедия'), (2,'Драма'), (3,'Мультфильм'), (4,'Триллер'), (5,'Документальный'), (6,'Боевик')
ON CONFLICT DO NOTHING;
