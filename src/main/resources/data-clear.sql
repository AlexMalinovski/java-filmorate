delete from user_friends;
delete from film_likes;
delete from film_genres;
delete from users;
delete from films;
delete from feed;
delete from reviews;
delete from review_reactions;
delete from directors;
delete from film_directors;

alter table feed alter column event_id restart with 1;
alter table films alter column id restart with 1;
alter table users alter column id restart with 1;
alter table directors alter column id restart with 1;
alter table reviews alter column id restart with 1;