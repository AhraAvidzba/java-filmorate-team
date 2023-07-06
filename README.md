# Filmorate
### Описание приложения
Доступны все CRUD операции с сущностями приложения. Приложение работает с фильмами и оценками пользователей, присутствует рекомендательная система, использующая 
алгоритм Slope One для коллаборативной фильтрации и вывода индивидуальной подборки рекомендованных фильмов 
пользователю по жанру и годам. В приложении присутствует лента событий, отзывы пользователей, а так же поиск 
по названию фильма и режиссеру.

### ER диаграмма
![image](src/main/resources/ER-diagram.png)

### Краткое описание сущностей
1. Таблица `users` содержит информацию о пользователях. В ней хранятся идентификатор пользователя (`user_id`),
   электронная почта (`email`), логин (`login`), имя (`name`) и дата рождения (`birthday`).

2. Таблица `mpa` содержит информацию о возрастных ограничениях фильмов. В ней хранятся идентификатор возрастного
   ограничения (`mpa_id`) и название (`mpa_name`).

3. Таблица `directors` содержит информацию о режиссерах. В ней хранятся идентификатор режиссера (`director_id`)
   и имя (`director_name`).

4. Таблица `film` содержит информацию о фильмах. В ней хранятся идентификатор фильма (`film_id`), название (`name`),
   описание (`description`), дата выхода (`release_date`), продолжительность (`duration`) и рейтинг (`rate`).

5. Таблица `film_director` содержит связь между фильмами и их режиссерами. В ней хранятся
   идентификатор режиссера (`director_id`) и идентификатор фильма (`film_id`).

6. Таблица `friend` содержит информацию о друзьях пользователей. В ней хранятся идентификатор пользователя (`user_id`),
   идентификатор друга (`friend_id`) и флаг подтверждения дружбы (`confirmed`).

7. Таблица `genre` содержит информацию о жанрах фильмов. В ней хранятся идентификатор жанра (`genre_id`)
   и название (`genre_name`).

8. Таблица `film_genre` содержит связь между фильмами и их жанрами. В ней хранятся идентификатор жанра (`genre_id`)
   и идентификатор фильма (`film_id`).

9. Таблица `likes` содержит информацию о лайках фильмов. В ней хранятся идентификатор фильма (`film_id`)
   и идентификатор пользователя (`user_id`).

10. Таблица `film_mpa` содержит связь между фильмами и их возрастными ограничениями. В ней хранятся идентификатор
    возрастного ограничения (`mpa_id`) и идентификатор фильма (`film_id`).

11. Таблица `reviews` содержит информацию о рецензиях на фильмы. В ней хранятся идентификатор рецензии (`review_id`),
    содержание (`content`), флаг положительной рецензии (`is_positive`), идентификатор пользователя (`user_id`),
    идентификатор фильма (`film_id`) и количество полезных голосов (`useful`).