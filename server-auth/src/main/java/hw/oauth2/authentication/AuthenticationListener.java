package hw.oauth2.authentication;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

public class AuthenticationListener implements ApplicationListener<AbstractAuthenticationEvent> {

    private final UserService loginAttemptsService;

    public AuthenticationListener(UserService loginAttemptsService) {
        this.loginAttemptsService = loginAttemptsService;
    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationSuccessEvent) {
            successfullLogin(event.getAuthentication());
            return;
        }
        if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            failedLogin(event.getAuthentication());
            return;
        }
    }

    private void successfullLogin(Authentication authentication) {
        String username = authentication.getName();
        loginAttemptsService.resetFailAttempts(username);
    }

    private void failedLogin(Authentication authentication) {
        String username = authentication.getName();
        loginAttemptsService.updateFailAttempts(username);
    }
}
