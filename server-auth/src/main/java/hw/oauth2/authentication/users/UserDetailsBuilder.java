package hw.oauth2.authentication.users;

import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;

import com.google.common.collect.ImmutableSet;

public class UserDetailsBuilder {

    private String userId;
    private String password;
    private Date passwordExpiresAt;
    private int failedLogins;
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

    public UserDetailsBuilder passwordExpiresAt(Date passwordExpiresAt) {
        this.passwordExpiresAt = passwordExpiresAt;
        return this;
    }

    public UserDetailsBuilder failedLogins(int failedLogins) {
        this.failedLogins = failedLogins;
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
        return new User(userId, getPassword(), isAccountEnabled(), true, !isPasswordExpired(), !isAccountLocked(),
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

    private boolean isAccountEnabled() {
        return StringUtils.hasText(password);
    }

    private boolean isPasswordExpired() {
        return passwordExpiresAt == null || !passwordExpiresAt.after(new Date());
    }

    private boolean isAccountLocked() {
        return failedLogins > 3;
    }
}
