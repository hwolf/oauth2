package hw.oauth2.services.admin;

import org.springframework.jdbc.core.JdbcTemplate;

import hw.oauth.messages.UpdateAction;
import hw.oauth.messages.user.UpdateUserMessage;

final class UpdateUser implements UpdateUserMessage.Visitor {

    private static final String SQL_INSERT_ENTRY = "insert into "
            + "t_user_entries (user_id, name, data) values (?, ?, ?)";

    private static final String SQL_DELETE_ENTRY = "delete from "
            + "t_user_entries where user_id = ? and name = ? and data = ?";

    private final UpdateUserMessage message;
    private final JdbcTemplate jdbcTemplate;

    UpdateUser(UpdateUserMessage message, JdbcTemplate jdbcTemplate) {
        this.message = message;
        this.jdbcTemplate = jdbcTemplate;
    }

    void updateUser() {
        message.visitChanges(this);
    }

    @Override
    public void visitAuthority(UpdateAction action, String authority) {
        updateEntry(action, "AUTHORITY", authority);
    }

    @Override
    public void visitRole(UpdateAction action, String role) {
        updateEntry(action, "ROLE", role);
    }

    private void updateEntry(UpdateAction action, String name, String value) {
        switch (action) {
        case ADD:
            jdbcTemplate.update(SQL_INSERT_ENTRY, message.getUserId(), name, value);
            break;
        case REMOVE:
            jdbcTemplate.update(SQL_DELETE_ENTRY, message.getUserId(), name, value);
            break;
        default:
            throw new IllegalArgumentException("UpdateAction " + action + " not supported");
        }
    }
}
