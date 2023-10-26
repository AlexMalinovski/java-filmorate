delete from user_friends;
delete from film_likes;
delete from film_genres;
delete from film_directors;
delete from users;
delete from films;
delete from directors;
delete from feed;

alter table feed alter column event_id restart with 1;
alter table films alter column id restart with 1;
alter table users alter column id restart with 1;