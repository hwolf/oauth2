package hw.oauth2.authentication;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.collect.ImmutableSet;

@SuppressWarnings("hiding")
public class UserDetailsBuilder {

    private String username;
    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;
    private String password;
    private final ImmutableSet.Builder<GrantedAuthority> authorities = ImmutableSet.builder();

    public String username() {
        return username;
    }

    public UserDetailsBuilder username(String username) {
        this.username = username;
        return this;
    }

    public UserDetailsBuilder enabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public UserDetailsBuilder accountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
        return this;
    }

    public UserDetailsBuilder accountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
        return this;
    }

    public UserDetailsBuilder credentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
        return this;
    }

    public UserDetailsBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserDetailsBuilder authorities(GrantedAuthority... authorities) {
        this.authorities.add(authorities);
        return this;
    }

    public UserDetailsBuilder authorities(Collection<GrantedAuthority> authorities) {
        this.authorities.addAll(authorities);
        return this;
    }

    public UserDetails build() {
        return new User(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
                authorities.build());
    }
}
