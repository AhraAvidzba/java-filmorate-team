package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> save(Film film);

    List<Film> getAll();

    Optional<Film> update(Film film);

    Optional<Film> updateById(Long id, Film film);

    void delete(Long id);

    Optional<Film> getById(Long id);

    void addLike(Long filmId, Long userId, Integer mark);

    void removeLike(Long filmId, Long userId);

    List<Film> getTheMostPopularFilms(int count);

    Film getFilmFull(Long id);

    List<Film> getFilmsLikedByUser(Long userId);

    List<Film> getFilmsByDirectorSortedByLikes(long directorId);

    List<Film> getFilmsByDirectorSortedByYear(long directorId);

    List<Film> findByDirectorsNameContainingIgnoreCase(String query);

    List<Film> findByTitleContainingIgnoreCase(String query);
}
