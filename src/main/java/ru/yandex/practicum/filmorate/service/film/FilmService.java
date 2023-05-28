package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    Film add(Film film);

    Film update(Film film);

    Film updateById(Long id, Film film);

    void deleteById(Long id);

    void addLike(Long id, Long userId, Integer mark);

    void removeLike(Long id, Long userId);

    List<Film> getPopularFilms(short count);

    List<Film> getAll();

    Film getById(Long id);

    Film getFilmFull(Long id);

    List<Film> getPopularFilmsByParameters(Short count, Long genreId, Integer year);

    List<Film> getCommonFilms(Long userId, Long friendId);

    List<Film> getFilmsByDirectorSortedByLikes(long directorId);

    List<Film> getFilmsByDirectorSortedByYear(long directorId);

    List<Film> findByTitleContainingIgnoreCase(String query);

    List<Film> findByDirectorsNameContainingIgnoreCase(String query);
}
