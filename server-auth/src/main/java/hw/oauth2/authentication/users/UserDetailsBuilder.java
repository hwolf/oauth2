package hw.oauth2.authentication.users;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import com.google.common.collect.ImmutableSet;

public class UserDetailsBuilder {

    private String userId;
    private String password;
    private boolean accountEnabled;
    private boolean passwordExpired;
    private boolean accountLocked;
    private final ImmutableSet.Builder<GrantedAuthority> authorities = ImmutableSet.builder();

    public String userId() {
        return userId;
    }

    public UserDetailsBuilder userId(String userId) {
        this.userId = userId;
        return this;
    }

    public UserDetailsBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserDetailsBuilder accountEnabled(boolean accountEnabled) {
        this.accountEnabled = accountEnabled;
        return this;
    }

    public UserDetailsBuilder passwordExpired(boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
        return this;
    }

    public UserDetailsBuilder accountLocked(boolean accountLocked) {
        this.accountLocked = accountLocked;
        return this;
    }

    public UserDetailsBuilder withAuthority(String authority) {
        authorities.add(new SimpleGrantedAuthority(authority));
        return this;
    }

    public UserDetailsBuilder withRole(String role) {
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        return this;
    }

    public UserDetails build() {
        return new User(userId, getPassword(), accountEnabled, true, !passwordExpired, !accountLocked,
                authorities.build());
    }

    private String getPassword() {
        if (!StringUtils.hasText(password)) {
            // if there is no password, account is disabled, but the class
            // org.springframework.security.core.userdetails.User needs a password.
            return "<no password>";
        }
        return password;
    }
}
