package hw.oauth2.authentication.users;

import java.time.Instant;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import hw.oauth2.entities.LoginStatusRepository;

@Transactional(readOnly = false)
public class UpdateLoginStatus implements ApplicationListener<AbstractAuthenticationEvent> {

    private final LoginStatusRepository loginStatusRepository;

    public UpdateLoginStatus(LoginStatusRepository loginStatusRepository) {
        this.loginStatusRepository = loginStatusRepository;
    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            successfulLogin(extractUserId(event));
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

    private void successfulLogin(String userId) {
        loginStatusRepository.loginSuccessful(userId, Instant.now());
    }

    private void failedLogin(String userId) {
        loginStatusRepository.loginFailed(userId, Instant.now());
    }
}
