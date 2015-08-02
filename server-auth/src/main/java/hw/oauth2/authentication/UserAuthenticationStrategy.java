package hw.oauth2.authentication;

import org.springframework.security.core.AuthenticationException;

import hw.oauth2.entities.User;

public interface UserAuthenticationStrategy {

    void authenticate(User user, Object credentials) throws AuthenticationException;
}
