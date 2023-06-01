package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Qualifier("userDbStorage")
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {
    private static final String INSERT_USER = "INSERT INTO users(email,login,name,birthday) VALUES (?,?,?,?)";
    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE user_id = ?";
    private static final String ADD_FRIEND = "INSERT INTO friend (user_id, friend_id, confirmed) VALUES (?,?,?)";
    private static final String DELETE_FRIEND = "DELETE FROM friend WHERE user_id=? AND friend_id=?";
    private static final String FIND_FRIENDS_BY_ID = "SELECT * FROM users WHERE user_id IN " +
            "(SELECT friend_id FROM friend WHERE user_id=? ORDER BY friend_id) ORDER BY user_id";
    private static final String DELETE_USER = "DELETE FROM users WHERE user_id = ?";
    private static final String FIND_COMMON_FRIENDS = "SELECT * FROM users " + "WHERE user_id IN " +
            "(SELECT friend_id FROM friend " +
            "WHERE user_id=? AND friend_id IN " +
            "(SELECT friend_id FROM friend " +
            "WHERE user_id=?) " +
            "ORDER BY friend_id) " +
            "ORDER BY user_id";
    private static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?" +
            " WHERE user_id = ?";
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Optional<User> save(User user) {
        try {
            long idUser = saveAndReturnId(user);
            user.setId(idUser);

            return Optional.of(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private long saveAndReturnId(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(INSERT_USER, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf((user.getBirthday())));
            return stmt;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query(FIND_ALL_USERS, new UserMapper(jdbcTemplate));
    }

    @Override
    public Optional<User> update(User user) {
        long userId = user.getId();
        int update = jdbcTemplate.update(UPDATE_USER, user.getEmail(), user.getLogin(),
                user.getName(), user.getBirthday(), userId);

        if (update == 0) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    @Override
    public Optional<User> updateById(Long id, User user) {
        return Optional.empty();
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update(DELETE_USER, id);
    }

    @Override
    public Optional<User> getById(Long id) {
        SqlRowSet userRs = jdbcTemplate.queryForRowSet(FIND_USER_BY_ID, id);
        if (userRs.next()) {
            User user = jdbcTemplate.queryForObject(FIND_USER_BY_ID, new UserMapper(jdbcTemplate), id);
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    public Optional<User> addFriend(Long userId, Long friendId) {
        if (getById(userId).isEmpty() || getById(friendId).isEmpty()) {
            return Optional.empty();
        }

        jdbcTemplate.update(ADD_FRIEND, userId, friendId, false);
        return Optional.ofNullable(getUser(friendId));
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        return new ArrayList<>(jdbcTemplate
                .query(FIND_COMMON_FRIENDS, new UserMapper(jdbcTemplate), userId, otherUserId));
    }

    private User getUser(Long id) {
        List<User> users = jdbcTemplate.query(FIND_USER_BY_ID, new UserMapper(jdbcTemplate), id);

        if (users.isEmpty()) {
            Optional.empty();
        } else {
            User user = users.get(0);
            List<User> friends = getFriends(id);
            user.setFriendsId(friends.stream().map(User::getId).collect(Collectors.toSet()));
            return user;
        }
        return jdbcTemplate.queryForObject(FIND_USER_BY_ID, new UserMapper(jdbcTemplate), id);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        jdbcTemplate.update(DELETE_FRIEND, userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        return new ArrayList<>(jdbcTemplate.query(FIND_FRIENDS_BY_ID, new UserMapper(jdbcTemplate), userId));
    }
}
