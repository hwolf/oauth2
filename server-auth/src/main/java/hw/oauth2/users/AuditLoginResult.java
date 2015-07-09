package hw.oauth2.users;

import java.util.Date;

import org.springframework.context.ApplicationListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = false)
public class AuditLoginResult implements ApplicationListener<AbstractAuthenticationEvent> {

    private static final String SQL_LOGIN_FAILED = "update " //
            + "    t_login_status " //
            + "set " //
            + "    failed_login_attempts = failed_login_attempts + 1, " //
            + "    last_failed_login = ? " //
            + "where " //
            + "    user_id = ?";

    private static final String SQL_LOGIN_SUCCESSFUL = "update " //
            + "    t_login_status " //
            + "set " //
            + "    failed_login_attempts = 0, " //
            + "    last_successful_login = ? " //
            + "where " //
            + "    user_id = ?";

    private final JdbcTemplate jdbcTemplate;

    public AuditLoginResult(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            successfullLogin(extractUserId(event));
            return;
        }
        if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            failedLogin(extractUserId(event));
            return;
        }
    }

    protected String extractUserId(AbstractAuthenticationEvent event) {
        Authentication authentication = event.getAuthentication();
        if (authentication == null) {
            return null;
        }
        return authentication.getName();
    }

    private void successfullLogin(String name) {
        jdbcTemplate.update(SQL_LOGIN_SUCCESSFUL, new Date(), name);

    }

    private void failedLogin(String name) {
        jdbcTemplate.update(SQL_LOGIN_FAILED, new Date(), name);
    }
}
