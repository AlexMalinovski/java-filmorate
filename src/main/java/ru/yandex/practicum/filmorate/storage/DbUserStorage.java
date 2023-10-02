package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.configs.AppProperties;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
@Component
@Primary
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final AppProperties appProperties;

    private User makeUser(ResultSet rs) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("user_id"))
                .name(rs.getString("user_name"))
                .email(rs.getString("user_email"))
                .birthday(rs.getDate("user_birthday").toLocalDate())
                .login(rs.getString("user_login"))
                .build();
        long friendId = rs.getLong("friend_id");
        if (friendId != 0) {
            user.addFriend(friendId);
        }
        return user;
    }

    private List<User> mapUserQueryResult(List<User> userQueryResult) {
        final List<User> buffer = new ArrayList<>();
        userQueryResult.forEach(u -> {
            int buffSize = buffer.size();
            if (buffSize == 0 || !Objects.equals(buffer.get(buffSize - 1).getId(), u.getId())) {
                buffer.add(u);
                return;
            }
            User curUser = buffer.get(buffSize - 1);
            curUser.addFriend(u.getFriends());
        });
        return buffer;
    }

    protected List<User> getUsers(int limit, int offset) {
        String sql = "SELECT u.id AS user_id, u.name AS user_name, u.email AS user_email, " +
                "u.birthday AS user_birthday, u.login AS user_login, " +
                "f.friend_id AS friend_id " +
                "FROM (SELECT * FROM users LIMIT ? OFFSET ?) AS u " +
                "LEFT JOIN user_friends AS f ON u.id=f.user_id " +
                "ORDER BY user_id";

        List<User> queryResult = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), limit, offset);
        return mapUserQueryResult(queryResult);
    }
    @Override
    public List<User> getAllUsers() {
        return getUsers(100, 0); //параметры - для будущей пагинации
    }

    @Override
    public User createUser(User user) {
        Map<String, Object> row = new HashMap<>();
        row.put("email", user.getEmail());
        row.put("login", user.getLogin());
        row.put("name", user.getName());
        row.put("birthday", user.getBirthday().format(appProperties.getDefaultDateFormatter()));

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        long id = simpleJdbcInsert.executeAndReturnKey(row).longValue();
        return getUserById(id)
                .orElseThrow(() -> new IllegalStateException("Ошибка при добавлении записи в БД"));
    }

    @Override
    public Optional<User> updateUser(User userUpdates) {
        String sql = "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id=?";
        jdbcTemplate.update(sql,
                userUpdates.getEmail(),
                userUpdates.getLogin(),
                userUpdates.getName(),
                userUpdates.getBirthday().format(appProperties.getDefaultDateFormatter()),
                userUpdates.getId());
        return getUserById(userUpdates.getId());
    }

    @Override
    public Optional<User> getUserById(long id) {
        String sql = "SELECT u.id AS user_id, u.name AS user_name, u.email AS user_email, " +
                "u.birthday AS user_birthday, u.login AS user_login, " +
                "f.friend_id AS friend_id " +
                "FROM users AS u " +
                "LEFT JOIN user_friends AS f ON u.id=f.user_id " +
                "WHERE u.id=?";

        List<User> queryResult = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
        if (queryResult.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(mapUserQueryResult(queryResult).get(0));
    }

    @Override
    public List<User> getUsersById(Set<Long> ids) {
        if (ids.size() == 0) {
            return new ArrayList<>();
        }
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String sql = "SELECT u.id AS user_id, u.name AS user_name, u.email AS user_email, " +
                "u.birthday AS user_birthday, u.login AS user_login, " +
                "f.friend_id AS friend_id " +
                "FROM users AS u " +
                "LEFT JOIN user_friends AS f ON u.id=f.user_id " +
                "WHERE u.id IN (%s) " +
                "ORDER BY user_id";

        List<User> queryResult = jdbcTemplate.query(String.format(sql, inSql), (rs, rowNum) -> makeUser(rs), ids.toArray());
        return mapUserQueryResult(queryResult);
    }

    @Override
    public void createFriend(long userId, long friendId) {
        Map<String, Object> row = new HashMap<>();
        row.put("user_id", userId);
        row.put("friend_id", friendId);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("user_friends");
        simpleJdbcInsert.execute(row);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sql = "DELETE FROM user_friends WHERE user_id=? AND friend_id=?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getUserFriends(long id) {
        String sql = "SELECT uf.friend_id AS user_id, u.name AS user_name, u.email AS user_email, u.birthday AS user_birthday, u.login AS user_login, " +
                "f.friend_id AS friend_id " +
                "FROM user_friends AS uf " +
                "LEFT JOIN users AS u ON uf.friend_id=u.id " +
                "LEFT JOIN user_friends AS f ON uf.friend_id=f.user_id " +
                "WHERE uf.user_id=? " +
                "ORDER BY user_id";

        List<User> queryResult =  jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
        return mapUserQueryResult(queryResult);
    }
}
