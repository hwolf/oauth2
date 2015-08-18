package oauth2.authentication;

import org.springframework.security.core.AuthenticationException;

import oauth2.entities.User;

public interface UserAuthenticationStrategy {

    void authenticate(User user, Object credentials) throws AuthenticationException;
}
