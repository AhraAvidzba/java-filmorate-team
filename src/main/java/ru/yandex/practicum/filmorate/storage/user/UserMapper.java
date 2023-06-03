package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;

//@UtilityClass
@RequiredArgsConstructor
public class UserMapper implements RowMapper<User> {
    private final JdbcTemplate jdbcTemplate;
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        String sqlFriends = "select friend_id from friend where user_id = ?";
        Long id = rs.getLong("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        User user = User.builder()
                .id(id)
                .email(email)
                .login(login)
                .name(name)
                .birthday(birthday)
                .build();

        user.setFriendsId(new HashSet<>(jdbcTemplate.query(sqlFriends, (rsFriends, rowNumb) -> friendId(rsFriends), id)));
        return user;
    }

    public Long friendId(ResultSet rs) throws SQLException {
        return rs.getLong("friend_id");
    }
}
