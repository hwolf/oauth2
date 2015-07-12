package hw.oauth2.services.admin;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.jdbc.core.JdbcTemplate;

import hw.oauth.messages.user.ChangePasswordMessage;

final class ChangePassword {

    private static final String SQL_CHANGE_PASSWORD = "update t_users " //
            + "set password = ?, password_expires_at = ? " //
            + "where user_id = ?";

    private final ChangePasswordMessage message;
    private final JdbcTemplate jdbcTemplate;

    ChangePassword(ChangePasswordMessage message, JdbcTemplate jdbcTemplate) {
        this.message = message;
        this.jdbcTemplate = jdbcTemplate;
    }

    void changePassword(Instant passwordExpiresAt) {
        jdbcTemplate.update(SQL_CHANGE_PASSWORD, message.getNewPassword(), Timestamp.from(passwordExpiresAt),
                message.getUserId());
    }
}
