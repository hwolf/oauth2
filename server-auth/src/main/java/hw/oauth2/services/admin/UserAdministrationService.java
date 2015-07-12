package hw.oauth2.services.admin;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import hw.oauth.messages.user.ChangePasswordMessage;
import hw.oauth.messages.user.CreateUserMessage;
import hw.oauth.messages.user.DeleteUserMessage;
import hw.oauth.messages.user.UpdateUserMessage;

@Transactional
public class UserAdministrationService {

    private final JdbcTemplate jdbcTemplate;

    public UserAdministrationService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void createUser(CreateUserMessage message) {
        Instant passwordExpiresAt = mapLocalDate(LocalDate.now());
        try {
            new InsertUser(message, jdbcTemplate).insertUser(passwordExpiresAt).insertAuthorities().insertStatusLogin();
        } catch (DuplicateKeyException ex) {
            throw new UserAlreadyExistsException("User " + message.getUserId() + " already exists", ex);
        }
    }

    public void deleteUser(DeleteUserMessage message) {
        new DeleteUser(message, jdbcTemplate).deleteUser();
    }

    public void updateUser(UpdateUserMessage message) {
        new UpdateUser(message, jdbcTemplate).updateUser();
    }

    public void changePassword(ChangePasswordMessage message) {
        Instant passwordExpiresAt = mapLocalDate(LocalDate.now().plusDays(90));
        new ChangePassword(message, jdbcTemplate).changePassword(passwordExpiresAt);
    }

    private Instant mapLocalDate(LocalDate date) {
        return Instant.from(date.atStartOfDay().atZone(ZoneOffset.ofHours(0)));
    }
}
