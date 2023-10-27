package ru.yandex.practicum.filmorate.storages;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.models.EventType;
import ru.yandex.practicum.filmorate.models.Operation;
import ru.yandex.practicum.filmorate.utils.AppProperties;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.utils.AppProperties;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Repository
@Primary
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final AppProperties appProperties;
    private final FeedStorage feedStorage;

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
        String sql = "select u.id as user_id, u.name as user_name, u.email as user_email, " +
                "u.birthday as user_birthday, u.login as user_login, " +
                "f.friend_id as friend_id " +
                "from (select * from users limit ? offset ?) as u " +
                "left join user_friends as f on u.id=f.user_id " +
                "order by user_id";

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
        String sql = "update users set email=?, login=?, name=?, birthday=? where id=?";
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
        String sql = "select u.id as user_id, u.name as user_name, u.email as user_email, " +
                "u.birthday as user_birthday, u.login as user_login, " +
                "f.friend_id as friend_id " +
                "from users as u " +
                "left join user_friends as f on u.id=f.user_id " +
                "where u.id=?";

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
        String sql = "select u.id as user_id, u.name as user_name, u.email as user_email, " +
                "u.birthday as user_birthday, u.login as user_login, " +
                "f.friend_id as friend_id " +
                "from users as u " +
                "left join user_friends as f on u.id=f.user_id " +
                "where u.id in (%s) " +
                "order by user_id";

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
        feedStorage.addEvent(userId, friendId, EventType.FRIEND, Operation.ADD);

    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String sql = "delete from user_friends where user_id=? and friend_id=?";
        jdbcTemplate.update(sql, userId, friendId);
        feedStorage.addEvent(userId, friendId, EventType.FRIEND, Operation.REMOVE);
    }

    @Override
    public List<User> getUserFriends(long id) {
        String sql = "select uf.friend_id as user_id, u.name as user_name, u.email as user_email, u.birthday as user_birthday, u.login as user_login, " +
                "f.friend_id as friend_id " +
                "from user_friends as uf " +
                "left join users as u on uf.friend_id=u.id " +
                "left join user_friends as f on uf.friend_id=f.user_id " +
                "where uf.user_id=? " +
                "order by user_id";

        List<User> queryResult = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);
        return mapUserQueryResult(queryResult);
    }
}
