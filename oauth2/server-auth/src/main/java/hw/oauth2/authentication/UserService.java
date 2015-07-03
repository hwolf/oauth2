package hw.oauth2.authentication;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    void updateFailAttempts(String username);

    void resetFailAttempts(String username);
}
