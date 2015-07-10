package hw.oauth2.services;

import java.sql.Timestamp;
import java.util.Set;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import hw.oauth.messages.user.CreateUserMessage;
import hw.oauth.messages.user.DeleteUserMessage;

@Transactional
public class UserAdministrationService {

    private static final String SQL_INSERT_USER = "insert into "
            + "t_users (user_id, password, password_expired) values (?, ?, ?)";

    private static final String SQL_INSERT_ENTRIES = "insert into "
            + "t_user_entries (user_id, name, data) values (?, ?, ?)";

    private static final String SQL_INSERT_LOGIN_STATUS = "insert into "
            + "t_login_status (user_id, failed_login_attempts) valueS (?, 0)";

    private static final String SQL_DELETE_USER = "delete from t_users where user_id = ?";

    private static final String SQL_DELETE_ENTRIES = "delete from t_user_entries where user_id = ?";

    private static final String SQL_DELETE_LOGIN_STATUS = "delete from t_login_status where user_id = ?";

    private final JdbcTemplate jdbcTemplate;

    public UserAdministrationService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createUser(CreateUserMessage message) {

        try {
            insertUser(message);
            insertAuthorities(message.getUserId(), message.getAuthorities());
            insertStatusLogin(message.getUserId());
        } catch (DuplicateKeyException ex) {
            throw new UserAlreadyExistsException("User " + message.getUserId() + " already exists", ex);
        }
    }

    private void insertUser(CreateUserMessage message) {
        jdbcTemplate.update(SQL_INSERT_USER, message.getUserId(), message.getPassword(),
                Timestamp.from(message.getPasswordExpiresAt()));
    }

    private void insertAuthorities(String userId, Set<String> authorities) {
        for (String authority : authorities) {
            jdbcTemplate.update(SQL_INSERT_ENTRIES, userId, "AUTHORITY", authority);
        }
    }

    private void insertStatusLogin(String userId) {
        jdbcTemplate.update(SQL_INSERT_LOGIN_STATUS, userId);
    }

    public void deleteUser(DeleteUserMessage message) {
        String userId = message.getUserId();
        jdbcTemplate.update(SQL_DELETE_LOGIN_STATUS, userId);
        jdbcTemplate.update(SQL_DELETE_ENTRIES, userId);
        jdbcTemplate.update(SQL_DELETE_USER, userId);
    }
}
