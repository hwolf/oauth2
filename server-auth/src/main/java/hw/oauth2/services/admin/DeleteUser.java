package hw.oauth2.services.admin;

import org.springframework.jdbc.core.JdbcTemplate;

import hw.oauth.messages.user.DeleteUserMessage;

final class DeleteUser {

    private static final String SQL_DELETE_USER = "delete from t_users where user_id = ?";

    private static final String SQL_DELETE_ENTRIES = "delete from t_user_entries where user_id = ?";

    private static final String SQL_DELETE_LOGIN_STATUS = "delete from t_login_status where user_id = ?";

    private final DeleteUserMessage message;
    private final JdbcTemplate jdbcTemplate;

    DeleteUser(DeleteUserMessage message, JdbcTemplate jdbcTemplate) {
        this.message = message;
        this.jdbcTemplate = jdbcTemplate;
    }

    void deleteUser() {
        String userId = message.getUserId();
        jdbcTemplate.update(SQL_DELETE_ENTRIES, userId);
        jdbcTemplate.update(SQL_DELETE_USER, userId);
        jdbcTemplate.update(SQL_DELETE_LOGIN_STATUS, userId);
    }
}
