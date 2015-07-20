package hw.oauth2.authentication.users;

import java.time.Instant;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Objects;

import hw.oauth2.Roles;
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
            successfulLogin(event.getAuthentication());
            return;
        }
        if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            failedLogin(event.getAuthentication());
            return;
        }
    }

    private void successfulLogin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (Objects.equal("ROLE_" + Roles.AUTHENTICATED, authority.getAuthority())) {
                loginStatusRepository.loginSuccessful(authentication.getName(), Instant.now());
                return;
            }
        }
    }

    private void failedLogin(Authentication authentication) {
        if (authentication == null) {
            return;
        }
        loginStatusRepository.loginFailed(authentication.getName(), Instant.now());
    }
}
