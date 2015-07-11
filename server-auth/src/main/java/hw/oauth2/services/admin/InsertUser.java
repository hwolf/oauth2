package hw.oauth2.services.admin;

import java.sql.Timestamp;

import org.springframework.jdbc.core.JdbcTemplate;

import hw.oauth.messages.user.CreateUserMessage;

final class InsertUser {

    private static final String SQL_INSERT_USER = "insert into "
            + "t_users (user_id, password, password_expired) values (?, ?, ?)";

    private static final String SQL_INSERT_ENTRY = "insert into "
            + "t_user_entries (user_id, name, data) values (?, ?, ?)";

    private static final String SQL_INSERT_LOGIN_STATUS = "insert into "
            + "t_login_status (user_id, failed_login_attempts) valueS (?, 0)";

    private final CreateUserMessage message;
    private final JdbcTemplate jdbcTemplate;

    InsertUser(CreateUserMessage message, JdbcTemplate jdbcTemplate) {
        this.message = message;
        this.jdbcTemplate = jdbcTemplate;
    }

    InsertUser insertUser() {
        jdbcTemplate.update(SQL_INSERT_USER, message.getUserId(), message.getPassword(),
                Timestamp.from(message.getPasswordExpiresAt()));
        return this;
    }

    InsertUser insertAuthorities() {
        for (String authority : message.getAuthorities()) {
            jdbcTemplate.update(SQL_INSERT_ENTRY, message.getUserId(), "AUTHORITY", authority);
            return this;
        }
        return this;
    }

    InsertUser insertStatusLogin() {
        jdbcTemplate.update(SQL_INSERT_LOGIN_STATUS, message.getUserId());
        return this;
    }
}
