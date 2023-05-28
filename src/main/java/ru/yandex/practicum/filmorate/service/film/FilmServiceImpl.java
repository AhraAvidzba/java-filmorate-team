package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final FilmStorage filmStorage;
    private final DirectorStorage directorStorage;

    @Override
    public Film add(Film film) {
        Optional<Film> save = filmStorage.save(film);

        if (save.isEmpty()) {
            throw new IncorrectParameterException("Фильм с таким айди уже существует.");
        }

        return save.get();
    }

    @Override
    public Film update(Film film) {
        Optional<Film> update = filmStorage.update(film);

        if (update.isEmpty()) {
            throw new FilmNotFoundException("Несуществующий идентификатор фильма");
        }
        return update.get();
    }

    @Override
    public Film updateById(Long id, Film film) {
        Optional<Film> updateById = filmStorage.updateById(id, film);

        if (updateById.isEmpty()) {
            throw new FilmNotFoundException("Несуществующий идентификатор фильма");
        }

        return updateById.get();
    }

    @Override
    public void deleteById(Long id) {
        filmStorage.delete(id);
    }

    @Override
    public void addLike(Long id, Long userId, Integer mark) {
        isUserIdPositive(userId);

        Optional<Film> filmStorageById = filmStorage.getById(id);

        if (filmStorageById.isEmpty()) {
            throw new FilmNotFoundException("Ошибка, данный фильм не найден.");
        }
        filmStorage.addLike(id, userId, mark);
        Film film = filmStorageById.get();
        film.setRate(film.getRate() + 1);

        Map<Long, Integer> usersWhoLike = film.getUsersWhoLike();

        if (usersWhoLike == null) {
            return;
        }

        usersWhoLike.put(userId, mark);
        film.setUsersWhoLike(usersWhoLike);

        filmStorage.update(film);
    }

    private static void isUserIdPositive(Long userId) {
        if (userId < 0) {
            throw new FilmNotFoundException("Айди не может быть отрицательным");
        }
    }

    @Override
    public void removeLike(Long id, Long userId) {
        isUserIdPositive(userId);

        Optional<Film> filmStorageById = filmStorage.getById(id);

        if (filmStorageById.isEmpty()) {
            throw new FilmNotFoundException("Ошибка, данный фильм не найден.");
        }

        filmStorage.removeLike(id, userId);

        Film film = filmStorageById.get();

        if (film.getRate() > 1) {
            film.setRate(film.getRate() - 1);

            Map<Long, Integer> usersWhoLike = film.getUsersWhoLike();

            if (usersWhoLike == null) {
                return;
            }

            usersWhoLike.remove(userId);
            film.setUsersWhoLike(usersWhoLike);

            filmStorage.update(film);
        }
    }

    @Override
    public List<Film> getPopularFilms(short count) {

        List<Film> all = filmStorage.getTheMostPopularFilms(count);

        if (all == null) {
            return new ArrayList<>();
        }

        List<Film> chartFilms = all.stream()
                .sorted(Comparator.comparing(Film::getRate).reversed())
                .limit(count)
                .collect(Collectors.toList());

        return chartFilms;
    }

    @Override
    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    @Override
    public Film getById(Long id) {
        Optional<Film> filmOptional = filmStorage.getById(id);

        if (filmOptional.isEmpty()) {
            throw new FilmNotFoundException("Фильма с таким айди нет.");
        }

        return filmOptional.get();
    }

    @Override
    public Film getFilmFull(Long id) {
        Film filmFull = filmStorage.getFilmFull(id);

        if (filmFull == null) {
            throw new FilmNotFoundException("Фильма с таким айди нет.");
        }
        return filmFull;
    }

    @Override
    public List<Film> getPopularFilmsByParameters(Short count, Long genreId, Integer year) {
        if (genreId == 0 && year == 0) return getPopularFilms(count);
        List<Film> films = filmStorage.getTheMostPopularFilms(Short.MAX_VALUE);
        if (genreId > 0) {
            films.removeIf(f -> !(getGenreIdsForCurrentFilm(f).contains(genreId)));
        }
        if (year > 0) {
            films.removeIf(f -> f.getReleaseDate().getYear() != year);
        }
        return films.stream().sorted(Comparator.comparing(Film::sumLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getCommonFilms(Long userId, Long friendId) {
        List<Film> userFilms = filmStorage.getFilmsLikedByUser(userId);
        List<Film> friendFilms = filmStorage.getFilmsLikedByUser(friendId);
        List<Long> friendFilmsIds = friendFilms.stream()
                .map(Film::getId)
                .collect(Collectors.toList());
        return userFilms.stream()
                .filter(x -> friendFilmsIds.contains(x.getId()))
                .collect(Collectors.toList());
    }

    private List<Long> getGenreIdsForCurrentFilm(Film film) {
        Set<Genre> genres = film.getGenres();
        return genres.stream().map(Genre::getId).collect(Collectors.toList());
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByLikes(long directorId) {
        directorStorage.getDirectorById(directorId);
        return filmStorage.getFilmsByDirectorSortedByLikes(directorId);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByYear(long directorId) {
        directorStorage.getDirectorById(directorId);
        return filmStorage.getFilmsByDirectorSortedByYear(directorId);
    }

    @Override
    public List<Film> findByDirectorsNameContainingIgnoreCase(String query) {
        return filmStorage.findByDirectorsNameContainingIgnoreCase(query);
    }

    @Override
    public List<Film> findByTitleContainingIgnoreCase(String query) {
        return filmStorage.findByTitleContainingIgnoreCase(query);
    }
}
